package com.ptu.common.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 统一API响应结果封装
 * 兼容所有微服务，字段包含：code、message、data、timestamp、success
 */
@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class R<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    private int code;

    /**
     * 消息
     */
    private String message;

    /**
     * 数据
     */
    private T data;

    /**
     * 时间戳
     */
    private long timestamp;

    /**
     * 是否成功
     */
    private boolean success;

    private R() {
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 成功
     *
     * @param <T> 数据类型
     * @return 响应结果
     */
    public static <T> R<T> ok() {
        return ok(null);
    }

    /**
     * 成功
     *
     * @param data 数据
     * @param <T>  数据类型
     * @return 响应结果
     */
    public static <T> R<T> ok(T data) {
        return ok(data, "操作成功");
    }

    /**
     * 成功
     *
     * @param data    数据
     * @param message 消息
     * @param <T>     数据类型
     * @return 响应结果
     */
    public static <T> R<T> ok(T data, String message) {
        R<T> r = new R<>();
        r.setCode(200);
        r.setData(data);
        r.setMessage(message);
        r.setSuccess(true);
        return r;
    }

    /**
     * 失败
     *
     * @param <T> 数据类型
     * @return 响应结果
     */
    public static <T> R<T> failed() {
        return failed("操作失败");
    }

    /**
     * 失败
     *
     * @param message 消息
     * @param <T>     数据类型
     * @return 响应结果
     */
    public static <T> R<T> failed(String message) {
        return failed(500, message);
    }

    /**
     * 失败
     *
     * @param code    状态码
     * @param message 消息
     * @param <T>     数据类型
     * @return 响应结果
     */
    public static <T> R<T> failed(int code, String message) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setMessage(message);
        r.setSuccess(false);
        return r;
    }

    /**
     * 参数错误
     *
     * @param <T> 数据类型
     * @return 响应结果
     */
    public static <T> R<T> paramError() {
        return failed(ResultCode.PARAM_ERROR.getCode(), ResultCode.PARAM_ERROR.getMessage());
    }

    /**
     * 参数错误
     *
     * @param message 消息
     * @param <T>     数据类型
     * @return 响应结果
     */
    public static <T> R<T> paramError(String message) {
        return new R<T>()
                .setCode(ResultCode.PARAM_ERROR.getCode())
                .setMessage(message);
    }

    /**
     * 未授权
     *
     * @param <T> 数据类型
     * @return 响应结果
     */
    public static <T> R<T> unauthorized() {
        return failed(ResultCode.UNAUTHORIZED.getCode(), ResultCode.UNAUTHORIZED.getMessage());
    }

    /**
     * 未授权
     *
     * @param message 消息
     * @param <T>     数据类型
     * @return 响应结果
     */
    public static <T> R<T> unauthorized(String message) {
        return new R<T>()
                .setCode(ResultCode.UNAUTHORIZED.getCode())
                .setMessage(message);
    }

    /**
     * 权限不足
     *
     * @param <T> 数据类型
     * @return 响应结果
     */
    public static <T> R<T> forbidden() {
        return failed(ResultCode.FORBIDDEN.getCode(), ResultCode.FORBIDDEN.getMessage());
    }

    /**
     * 权限不足
     *
     * @param message 消息
     * @param <T>     数据类型
     * @return 响应结果
     */
    public static <T> R<T> forbidden(String message) {
        return new R<T>()
                .setCode(ResultCode.FORBIDDEN.getCode())
                .setMessage(message);
    }

    /**
     * 资源不存在
     *
     * @param <T> 数据类型
     * @return 响应结果
     */
    public static <T> R<T> notFound() {
        return failed(ResultCode.NOT_FOUND.getCode(), ResultCode.NOT_FOUND.getMessage());
    }

    /**
     * 资源不存在
     *
     * @param message 消息
     * @param <T>     数据类型
     * @return 响应结果
     */
    public static <T> R<T> notFound(String message) {
        return new R<T>()
                .setCode(ResultCode.NOT_FOUND.getCode())
                .setMessage(message);
    }

    /**
     * 服务器内部错误
     *
     * @param <T> 数据类型
     * @return 响应结果
     */
    public static <T> R<T> error() {
        return failed(ResultCode.INTERNAL_SERVER_ERROR.getCode(), ResultCode.INTERNAL_SERVER_ERROR.getMessage());
    }

    /**
     * 服务器内部错误
     *
     * @param message 消息
     * @param <T>     数据类型
     * @return 响应结果
     */
    public static <T> R<T> error(String message) {
        return new R<T>()
                .setCode(ResultCode.INTERNAL_SERVER_ERROR.getCode())
                .setMessage(message);
    }

    /**
     * 判断响应是否成功
     *
     * @return 是否成功
     */
    public boolean isSuccess() {
        return this.success;
    }

    /**
     * 修正ResultCode重载调用
     *
     * @param resultCode 结果码
     * @param <T>        数据类型
     * @return 响应结果
     */
    public static <T> R<T> failed(IResultCode resultCode) {
        return failed(resultCode.getCode(), resultCode.getMessage());
    }
} 