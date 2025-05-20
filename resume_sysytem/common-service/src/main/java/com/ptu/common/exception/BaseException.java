package com.ptu.common.exception;

import com.ptu.common.api.IResultCode;
import lombok.Getter;

/**
 * 基础异常类，所有自定义异常继承此类
 */
@Getter
public class BaseException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private final IResultCode resultCode;

    /**
     * 构造函数
     *
     * @param resultCode 错误码
     */
    public BaseException(IResultCode resultCode) {
        super(resultCode.getMessage());
        this.resultCode = resultCode;
    }

    /**
     * 构造函数
     *
     * @param resultCode 错误码
     * @param message 错误消息
     */
    public BaseException(IResultCode resultCode, String message) {
        super(message);
        this.resultCode = resultCode;
    }

    /**
     * 构造函数
     *
     * @param resultCode 错误码
     * @param cause 异常原因
     */
    public BaseException(IResultCode resultCode, Throwable cause) {
        super(resultCode.getMessage(), cause);
        this.resultCode = resultCode;
    }

    /**
     * 构造函数
     *
     * @param resultCode 错误码
     * @param message 错误消息
     * @param cause 异常原因
     */
    public BaseException(IResultCode resultCode, String message, Throwable cause) {
        super(message, cause);
        this.resultCode = resultCode;
    }
} 