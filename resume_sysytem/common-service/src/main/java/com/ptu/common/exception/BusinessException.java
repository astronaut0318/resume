package com.ptu.common.exception;

import com.ptu.common.api.IResultCode;
import com.ptu.common.api.ResultCode;

/**
 * 业务异常
 */
public class BusinessException extends BaseException {

    private static final long serialVersionUID = 1L;

    /**
     * 构造函数
     */
    public BusinessException() {
        super(ResultCode.FAILURE);
    }

    /**
     * 构造函数
     *
     * @param message 错误消息
     */
    public BusinessException(String message) {
        super(ResultCode.FAILURE, message);
    }

    /**
     * 构造函数
     *
     * @param resultCode 错误码
     */
    public BusinessException(IResultCode resultCode) {
        super(resultCode);
    }

    /**
     * 构造函数
     *
     * @param resultCode 错误码
     * @param message    错误消息
     */
    public BusinessException(IResultCode resultCode, String message) {
        super(resultCode, message);
    }

    /**
     * 构造函数
     *
     * @param resultCode 错误码
     * @param cause      异常原因
     */
    public BusinessException(IResultCode resultCode, Throwable cause) {
        super(resultCode, cause);
    }

    /**
     * 构造函数
     *
     * @param resultCode 错误码
     * @param message    错误消息
     * @param cause      异常原因
     */
    public BusinessException(IResultCode resultCode, String message, Throwable cause) {
        super(resultCode, message, cause);
    }
} 