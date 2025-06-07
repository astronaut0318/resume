package com.ptu.common.constants;

/**
 * 订单相关常量
 */
public class OrderConstants {

    /**
     * 订单状态
     */
    public static final class OrderStatus {
        /**
         * 待支付
         */
        public static final Integer UNPAID = 0;
        
        /**
         * 已支付
         */
        public static final Integer PAID = 1;
        
        /**
         * 已取消
         */
        public static final Integer CANCELED = 2;
    }
    
    /**
     * 支付方式
     */
    public static final class PayType {
        /**
         * 支付宝
         */
        public static final Integer ALIPAY = 1;
        
        /**
         * 微信
         */
        public static final Integer WECHAT = 2;
        
        /**
         * 免费（无需支付）
         */
        public static final Integer FREE = 0;
    }
    
    /**
     * 支付状态
     */
    public static final class PaymentStatus {
        /**
         * 处理中
         */
        public static final Integer PROCESSING = 0;
        
        /**
         * 支付成功
         */
        public static final Integer SUCCESS = 1;
        
        /**
         * 支付失败
         */
        public static final Integer FAILED = 2;
    }
} 