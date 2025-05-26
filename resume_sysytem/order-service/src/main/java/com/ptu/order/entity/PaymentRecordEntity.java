package com.ptu.order.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 支付记录实体类，对应数据库payment_records表
 */
@Data
@TableName("payment_records")
public class PaymentRecordEntity {
    /** 支付记录主键ID */
    @TableId
    private Long id;

    /** 订单编号 */
    private String orderNo;

    /** 交易流水号 */
    private String tradeNo;

    /** 支付方式：1-支付宝，2-微信 */
    private Integer payType;

    /** 支付金额 */
    private BigDecimal amount;

    /** 状态：0-处理中，1-成功，2-失败 */
    private Integer status;

    /** 创建时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;
} 