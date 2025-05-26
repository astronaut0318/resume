package com.ptu.order.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 订单创建请求DTO
 */
@Data
public class OrderCreateDTO implements Serializable {
    /** 模板ID，必填 */
    @NotNull(message = "模板ID不能为空")
    private Long templateId;

    /** 支付方式：1-支付宝，2-微信，必填 */
    @NotNull(message = "支付方式不能为空")
    private Integer payType;
} 