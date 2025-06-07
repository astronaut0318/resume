package com.ptu.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ptu.common.exception.BusinessException;
import com.ptu.common.utils.IdGenerator;
import com.ptu.common.constants.OrderConstants;
import com.ptu.order.dto.OrderCreateDTO;
import com.ptu.order.entity.OrderEntity;
import com.ptu.order.entity.PaymentRecordEntity;
import com.ptu.order.mapper.OrderMapper;
import com.ptu.order.mapper.PaymentRecordMapper;
import com.ptu.order.service.OrderService;
import com.ptu.order.vo.OrderVO;
import com.ptu.order.vo.PaymentVO;
import com.ptu.order.template.client.TemplateClient;
import com.ptu.order.template.dto.TemplateDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单服务实现类，实现订单核心业务逻辑
 */
@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private PaymentRecordMapper paymentRecordMapper;
    
    @Autowired
    private TemplateClient templateClient;
    
    @Autowired
    private IdGenerator idGenerator;

    /**
     * 创建订单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO createOrder(OrderCreateDTO dto, Long userId) {
        // 1. 调用模板服务获取模板信息
        TemplateDTO template = templateClient.getTemplateById(dto.getTemplateId());
        if (template == null) {
            throw new BusinessException("模板不存在");
        }
        
        // 2. 检查模板是否可购买
        if (template.getStatus() != 1) {
            throw new BusinessException("模板已下架");
        }
        
        // 3. 生成订单号
        String orderNo = idGenerator.generateOrderNo();
        
        // 4. 创建订单
        OrderEntity order = new OrderEntity();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTemplateId(dto.getTemplateId());
        order.setAmount(template.getPrice());
        order.setStatus(OrderConstants.OrderStatus.UNPAID); // 待支付
        order.setCreateTime(new Date());
        order.setUpdateTime(new Date());
        
        // 如果是免费模板，直接设为已支付
        if (template.getIsFree() == 1 || BigDecimal.ZERO.compareTo(template.getPrice()) == 0) {
            order.setStatus(OrderConstants.OrderStatus.PAID);
            order.setPayTime(new Date());
            order.setPayType(OrderConstants.PayType.FREE);
        } else {
            // 设置支付方式
            order.setPayType(dto.getPayType());
        }
        
        // 保存订单
        orderMapper.insert(order);
        
        // 5. 转换为VO并返回
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(order, orderVO);
        orderVO.setTemplateName(template.getName());
        
        return orderVO;
    }

    /**
     * 查询订单列表
     */
    @Override
    public List<OrderVO> listOrders(Long userId, Integer status, int page, int size) {
        // 1. 构建查询条件
        LambdaQueryWrapper<OrderEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderEntity::getUserId, userId);
        
        // 2. 如果状态参数不为空，添加状态过滤条件
        if (status != null) {
            queryWrapper.eq(OrderEntity::getStatus, status);
        }
        
        // 3. 按创建时间倒序排序
        queryWrapper.orderByDesc(OrderEntity::getCreateTime);
        
        // 4. 分页查询
        IPage<OrderEntity> orderPage = new Page<>(page, size);
        orderPage = orderMapper.selectPage(orderPage, queryWrapper);
        
        if (orderPage.getRecords().isEmpty()) {
            return Collections.emptyList();
        }
        
        // 5. 获取订单中涉及的所有模板ID
        List<Long> templateIds = orderPage.getRecords().stream()
                .map(OrderEntity::getTemplateId)
                .collect(Collectors.toList());
        
        // 6. 批量获取模板信息
        List<TemplateDTO> templates = templateClient.getTemplatesByIds(templateIds);
        
        // 7. 转换为VO并返回
        return orderPage.getRecords().stream().map(order -> {
            OrderVO vo = new OrderVO();
            BeanUtils.copyProperties(order, vo);
            
            // 设置模板名称
            templates.stream()
                    .filter(t -> t.getId().equals(order.getTemplateId()))
                    .findFirst()
                    .ifPresent(t -> vo.setTemplateName(t.getName()));
            
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 获取订单详情
     */
    @Override
    public OrderVO getOrderDetail(String orderNo, Long userId) {
        // 1. 查询订单
        LambdaQueryWrapper<OrderEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderEntity::getOrderNo, orderNo);
        queryWrapper.eq(OrderEntity::getUserId, userId);
        OrderEntity order = orderMapper.selectOne(queryWrapper);
        
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        // 2. 获取模板信息
        TemplateDTO template = templateClient.getTemplateById(order.getTemplateId());
        
        // 3. 转换为VO并返回
        OrderVO vo = new OrderVO();
        BeanUtils.copyProperties(order, vo);
        
        if (template != null) {
            vo.setTemplateName(template.getName());
        }
        
        return vo;
    }

    /**
     * 取消订单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(String orderNo, Long userId) {
        // 1. 查询订单
        LambdaQueryWrapper<OrderEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderEntity::getOrderNo, orderNo);
        queryWrapper.eq(OrderEntity::getUserId, userId);
        OrderEntity order = orderMapper.selectOne(queryWrapper);
        
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        // 2. 检查订单状态
        if (order.getStatus() != OrderConstants.OrderStatus.UNPAID) {
            throw new BusinessException("只有待支付订单可以取消");
        }
        
        // 3. 更新订单状态
        order.setStatus(OrderConstants.OrderStatus.CANCELED);
        order.setUpdateTime(new Date());
        orderMapper.updateById(order);
    }

    /**
     * 处理支付回调
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handlePaymentNotify(String orderNo, Integer payType, String notifyData) {
        // 1. 查询订单
        LambdaQueryWrapper<OrderEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderEntity::getOrderNo, orderNo);
        OrderEntity order = orderMapper.selectOne(queryWrapper);
        
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        // 2. 检查订单状态
        if (order.getStatus() != OrderConstants.OrderStatus.UNPAID) {
            // 订单已支付或已取消，直接返回
            return;
        }
        
        // 3. 解析支付回调数据
        // 实际项目中应根据不同支付方式解析不同的回调数据
        // 这里简化处理，认为支付成功
        String tradeNo = parseTradeNo(notifyData, payType);
        
        // 4. 更新订单状态
        order.setStatus(OrderConstants.OrderStatus.PAID);
        order.setPayType(payType);
        order.setPayTime(new Date());
        order.setUpdateTime(new Date());
        orderMapper.updateById(order);
        
        // 5. 记录支付流水
        PaymentRecordEntity record = new PaymentRecordEntity();
        record.setOrderNo(orderNo);
        record.setTradeNo(tradeNo);
        record.setPayType(payType);
        record.setAmount(order.getAmount());
        record.setStatus(OrderConstants.PaymentStatus.SUCCESS);
        record.setCreateTime(new Date());
        record.setUpdateTime(new Date());
        paymentRecordMapper.insert(record);
    }

    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        return idGenerator.generateOrderNo();
    }
    
    /**
     * 解析交易流水号
     * 不同支付方式有不同的解析方法，这里简化处理
     */
    private String parseTradeNo(String notifyData, Integer payType) {
        // 实际项目中应根据支付方式和回调数据解析交易流水号
        // 这里简化处理，生成一个模拟的流水号
        String prefix = payType == OrderConstants.PayType.ALIPAY ? "ALI" : "WX";
        return idGenerator.generateTradeNo(prefix);
    }

    /**
     * 处理订单支付
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public PaymentVO processPayment(String orderNo, Long userId) throws Exception {
        log.info("开始处理订单支付: orderNo={}, userId={}", orderNo, userId);
        
        // 1. 查询订单
        LambdaQueryWrapper<OrderEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderEntity::getOrderNo, orderNo);
        queryWrapper.eq(OrderEntity::getUserId, userId);
        OrderEntity order = orderMapper.selectOne(queryWrapper);
        
        if (order == null) {
            log.error("订单不存在: orderNo={}, userId={}", orderNo, userId);
            throw new BusinessException("订单不存在");
        }
        
        // 2. 检查订单状态
        if (order.getStatus() == OrderConstants.OrderStatus.PAID) {
            log.info("订单已支付，直接返回支付成功: orderNo={}", orderNo);
            return buildPaymentResult(order, OrderConstants.PaymentStatus.SUCCESS, "订单已支付");
        }
        
        if (order.getStatus() == OrderConstants.OrderStatus.CANCELED) {
            log.error("订单已取消，不能支付: orderNo={}", orderNo);
            throw new BusinessException("订单已取消，不能支付");
        }
        
        try {
            // 3. 模拟支付处理
            log.info("模拟支付处理: orderNo={}, payType={}", orderNo, order.getPayType());
            
            // 生成支付流水号
            String prefix = order.getPayType() == OrderConstants.PayType.ALIPAY ? "ALI" : "WX";
            String tradeNo = idGenerator.generateTradeNo(prefix);
            
            // 4. 更新订单状态
            order.setStatus(OrderConstants.OrderStatus.PAID);
            order.setPayTime(new Date());
            order.setUpdateTime(new Date());
            orderMapper.updateById(order);
            
            // 5. 记录支付流水
            PaymentRecordEntity record = new PaymentRecordEntity();
            record.setOrderNo(orderNo);
            record.setTradeNo(tradeNo);
            record.setPayType(order.getPayType());
            record.setAmount(order.getAmount());
            record.setStatus(OrderConstants.PaymentStatus.SUCCESS);
            record.setCreateTime(new Date());
            record.setUpdateTime(new Date());
            paymentRecordMapper.insert(record);
            
            log.info("订单支付成功: orderNo={}, tradeNo={}", orderNo, tradeNo);
            
            // 6. 返回支付结果
            return buildPaymentResult(order, OrderConstants.PaymentStatus.SUCCESS, "支付成功", tradeNo);
        } catch (Exception e) {
            log.error("订单支付处理异常: orderNo={}", orderNo, e);
            
            // 记录支付失败流水
            PaymentRecordEntity record = new PaymentRecordEntity();
            record.setOrderNo(orderNo);
            record.setTradeNo("ERROR-" + System.currentTimeMillis());
            record.setPayType(order.getPayType());
            record.setAmount(order.getAmount());
            record.setStatus(OrderConstants.PaymentStatus.FAILED);
            record.setCreateTime(new Date());
            record.setUpdateTime(new Date());
            paymentRecordMapper.insert(record);
            
            throw new BusinessException("支付处理失败: " + e.getMessage());
        }
    }

    /**
     * 构建支付结果
     */
    private PaymentVO buildPaymentResult(OrderEntity order, Integer status, String message) {
        return buildPaymentResult(order, status, message, null);
    }
    
    /**
     * 构建支付结果（带交易流水号）
     */
    private PaymentVO buildPaymentResult(OrderEntity order, Integer status, String message, String tradeNo) {
        PaymentVO vo = new PaymentVO();
        vo.setOrderNo(order.getOrderNo());
        vo.setStatus(status);
        vo.setAmount(order.getAmount());
        vo.setPayType(order.getPayType());
        vo.setMessage(message);
        
        if (order.getPayTime() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            vo.setPayTime(sdf.format(order.getPayTime()));
        }
        
        if (tradeNo != null) {
            vo.setTradeNo(tradeNo);
        }
        
        // 模拟支付二维码URL
        if (status == OrderConstants.PaymentStatus.PROCESSING) {
            vo.setQrCodeUrl("https://example.com/pay/qrcode?orderNo=" + order.getOrderNo());
        }
        
        return vo;
    }
} 