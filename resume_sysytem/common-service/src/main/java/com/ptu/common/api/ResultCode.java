package com.ptu.common.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应码枚举
 */
@Getter
@AllArgsConstructor
public enum ResultCode implements IResultCode {

    /**
     * 成功
     */
    SUCCESS(200, "操作成功"),

    /**
     * 失败
     */
    FAILURE(500, "操作失败"),

    /**
     * 参数错误
     */
    PARAM_ERROR(400, "请求参数错误"),

    /**
     * 未授权
     */
    UNAUTHORIZED(401, "未授权"),

    /**
     * 权限不足
     */
    FORBIDDEN(403, "权限不足"),

    /**
     * 资源不存在
     */
    NOT_FOUND(404, "资源不存在"),

    /**
     * 服务器内部错误
     */
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),

    /**
     * 用户名或密码错误
     */
    USER_PASSWORD_ERROR(1001, "用户名或密码错误"),

    /**
     * 用户不存在
     */
    USER_NOT_FOUND(1002, "用户不存在"),

    /**
     * 用户已存在
     */
    USER_ALREADY_EXISTS(1003, "用户已存在"),

    /**
     * 用户未登录
     */
    USER_NOT_LOGIN(1004, "用户未登录");

    /**
     * 状态码
     */
    private final int code;

    /**
     * 消息
     */
    private final String message;
} 