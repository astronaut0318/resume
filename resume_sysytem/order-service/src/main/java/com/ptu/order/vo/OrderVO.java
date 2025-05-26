package com.ptu.order.vo;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单视图对象VO
 */
@Data
public class OrderVO implements Serializable {
    /** 订单编号 */
    private String orderNo;

    /** 模板ID */
    private Long templateId;

    /** 模板名称 */
    private String templateName;

    /** 订单金额 */
    private BigDecimal amount;

    /** 状态：0-待支付，1-已支付，2-已取消 */
    private Integer status;

    /** 支付方式 */
    private Integer payType;

    /** 支付时间 */
    private Date payTime;

    /** 创建时间 */
    private Date createTime;
} 