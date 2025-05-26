package com.ptu.order.exception;

import com.ptu.common.api.IResultCode;
import com.ptu.common.api.ResultCode;
import com.ptu.common.exception.BaseException;

/**
 * 订单业务异常类
 */
public class OrderException extends BaseException {
    /**
     * 默认使用通用失败码
     */
    public OrderException(String message) {
        super(ResultCode.FAILURE, message);
    }

    /**
     * 指定错误码和消息
     */
    public OrderException(IResultCode resultCode, String message) {
        super(resultCode, message);
    }

    /**
     * 仅指定错误码
     */
    public OrderException(IResultCode resultCode) {
        super(resultCode);
    }
} 