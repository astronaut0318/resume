package com.ptu.common.api;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 统一API响应结果封装
 */
@Data
@Accessors(chain = true)
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
        return new R<T>()
                .setCode(ResultCode.SUCCESS.getCode())
                .setMessage(ResultCode.SUCCESS.getMessage())
                .setData(data);
    }

    /**
     * 成功
     *
     * @param message 消息
     * @param data    数据
     * @param <T>     数据类型
     * @return 响应结果
     */
    public static <T> R<T> ok(String message, T data) {
        return new R<T>()
                .setCode(ResultCode.SUCCESS.getCode())
                .setMessage(message)
                .setData(data);
    }

    /**
     * 失败
     *
     * @param <T> 数据类型
     * @return 响应结果
     */
    public static <T> R<T> failed() {
        return failed(ResultCode.FAILURE);
    }

    /**
     * 失败
     *
     * @param resultCode 响应码
     * @param <T>        数据类型
     * @return 响应结果
     */
    public static <T> R<T> failed(IResultCode resultCode) {
        return new R<T>()
                .setCode(resultCode.getCode())
                .setMessage(resultCode.getMessage());
    }

    /**
     * 失败
     *
     * @param message 消息
     * @param <T>     数据类型
     * @return 响应结果
     */
    public static <T> R<T> failed(String message) {
        return new R<T>()
                .setCode(ResultCode.FAILURE.getCode())
                .setMessage(message);
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
        return new R<T>()
                .setCode(code)
                .setMessage(message);
    }

    /**
     * 参数错误
     *
     * @param <T> 数据类型
     * @return 响应结果
     */
    public static <T> R<T> paramError() {
        return failed(ResultCode.PARAM_ERROR);
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
        return failed(ResultCode.UNAUTHORIZED);
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
        return failed(ResultCode.FORBIDDEN);
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
        return failed(ResultCode.NOT_FOUND);
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
        return failed(ResultCode.INTERNAL_SERVER_ERROR);
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
        return this.code == ResultCode.SUCCESS.getCode();
    }
} 