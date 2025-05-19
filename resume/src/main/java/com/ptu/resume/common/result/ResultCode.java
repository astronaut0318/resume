package com.ptu.resume.common.result;

/**
 * API返回码
 */
public class ResultCode {
    /**
     * 成功
     */
    public static final int SUCCESS = 200;

    /**
     * 失败
     */
    public static final int ERROR = 500;

    /**
     * 未授权
     */
    public static final int UNAUTHORIZED = 401;

    /**
     * 禁止访问
     */
    public static final int FORBIDDEN = 403;

    /**
     * 参数错误
     */
    public static final int PARAM_ERROR = 400;

    /**
     * 不支持的请求类型
     */
    public static final int METHOD_NOT_ALLOWED = 405;

    /**
     * 请求超时
     */
    public static final int REQUEST_TIMEOUT = 408;
}