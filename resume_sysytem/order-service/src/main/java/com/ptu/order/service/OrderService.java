package com.ptu.order.service;

import com.ptu.order.dto.OrderCreateDTO;
import com.ptu.order.vo.OrderVO;
import com.ptu.order.vo.PaymentVO;
import java.util.List;

/**
 * 订单服务接口，定义订单相关核心业务方法
 */
public interface OrderService {
    /**
     * 创建订单
     * @param dto 订单创建参数
     * @param userId 用户ID
     * @return 订单VO
     */
    OrderVO createOrder(OrderCreateDTO dto, Long userId);

    /**
     * 查询订单列表
     * @param userId 用户ID
     * @param status 订单状态（可选）
     * @param page 页码
     * @param size 每页大小
     * @return 订单VO列表
     */
    List<OrderVO> listOrders(Long userId, Integer status, int page, int size);

    /**
     * 获取订单详情
     * @param orderNo 订单编号
     * @param userId 用户ID
     * @return 订单VO
     */
    OrderVO getOrderDetail(String orderNo, Long userId);

    /**
     * 取消订单
     * @param orderNo 订单编号
     * @param userId 用户ID
     */
    void cancelOrder(String orderNo, Long userId);
    
    /**
     * 处理订单支付
     * @param orderNo 订单编号
     * @param userId 用户ID
     * @return 支付结果
     * @throws Exception 支付处理异常
     */
    PaymentVO processPayment(String orderNo, Long userId) throws Exception;

    /**
     * 处理支付回调
     * @param orderNo 订单编号
     * @param payType 支付方式
     * @param notifyData 回调数据
     */
    void handlePaymentNotify(String orderNo, Integer payType, String notifyData);
} 