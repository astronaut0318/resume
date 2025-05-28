package com.ptu.order.controller;

import com.ptu.order.dto.OrderCreateDTO;
import com.ptu.order.vo.OrderVO;
import com.ptu.order.service.OrderService;
import com.ptu.common.api.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;

/**
 * 订单控制器，提供订单相关RESTful接口
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 创建订单
     */
    @PostMapping
    public R<OrderVO> createOrder(@Valid @RequestBody OrderCreateDTO dto) {
        // TODO: 获取当前登录用户ID
        Long userId = 1L;
        OrderVO vo = orderService.createOrder(dto, userId);
        return R.ok(vo);
    }

    /**
     * 查询订单列表
     */
    @GetMapping
    public R<List<OrderVO>> listOrders(@RequestParam(required = false) Integer status,
                                       @RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "10") int size) {
        Long userId = 1L; // TODO: 获取当前登录用户ID
        List<OrderVO> list = orderService.listOrders(userId, status, page, size);
        return R.ok(list);
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/{orderNo}")
    public R<OrderVO> getOrderDetail(@PathVariable String orderNo) {
        Long userId = 1L; // TODO: 获取当前登录用户ID
        OrderVO vo = orderService.getOrderDetail(orderNo, userId);
        return R.ok(vo);
    }

    /**
     * 取消订单
     */
    @PutMapping("/{orderNo}/cancel")
    public R<Void> cancelOrder(@PathVariable String orderNo) {
        Long userId = 1L; // TODO: 获取当前登录用户ID
        orderService.cancelOrder(orderNo, userId);
        return R.ok(null, "取消成功");
    }

    /**
     * 支付回调
     */
    @PostMapping("/notify/{payType}")
    public R<Void> handlePaymentNotify(@PathVariable Integer payType, @RequestBody String notifyData) {
        // TODO: 解析orderNo
        String orderNo = "";
        orderService.handlePaymentNotify(orderNo, payType, notifyData);
        return R.ok(null, "回调处理成功");
    }
} 