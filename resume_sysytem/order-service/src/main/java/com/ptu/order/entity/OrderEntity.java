package com.ptu.order.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单实体类，对应数据库orders表
 */
@Data
@TableName("orders")
public class OrderEntity {
    /** 订单主键ID */
    @TableId
    private Long id;

    /** 订单编号 */
    private String orderNo;

    /** 用户ID */
    private Long userId;

    /** 模板ID */
    private Long templateId;

    /** 订单金额 */
    private BigDecimal amount;

    /** 支付方式：1-支付宝，2-微信 */
    private Integer payType;

    /** 状态：0-待支付，1-已支付，2-已取消 */
    private Integer status;

    /** 支付时间 */
    private Date payTime;

    /** 创建时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;
} 