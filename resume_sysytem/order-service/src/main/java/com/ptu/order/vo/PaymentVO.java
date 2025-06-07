package com.ptu.order.vo;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 支付结果视图对象
 */
@Data
public class PaymentVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单号
     */
    private String orderNo;
    
    /**
     * 支付状态：1-成功，0-处理中，-1-失败
     */
    private Integer status;
    
    /**
     * 支付金额
     */
    private BigDecimal amount;
    
    /**
     * 支付方式：1-支付宝，2-微信
     */
    private Integer payType;
    
    /**
     * 支付时间
     */
    private String payTime;
    
    /**
     * 支付流水号
     */
    private String tradeNo;
    
    /**
     * 支付二维码URL
     */
    private String qrCodeUrl;
    
    /**
     * 支付结果描述
     */
    private String message;
} 