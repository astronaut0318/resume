package com.ptu.common.api;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 统一API响应结果封装
 *
 * @param <T> 数据类型
 */
@Data
@Accessors(chain = true)
public class R<T> implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 返回消息
     */
    private String message;

    /**
     * 返回数据
     */
    private T data;

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 构造私有化
     */
    private R() {
    }

    /**
     * 成功返回结果
     *
     * @param <T> 数据类型
     * @return 结果对象
     */
    public static <T> R<T> ok() {
        return new R<T>()
                .setCode(ResultCode.SUCCESS.getCode())
                .setMessage(ResultCode.SUCCESS.getMessage())
                .setSuccess(true);
    }

    /**
     * 成功返回结果
     *
     * @param data 返回数据
     * @param <T>  数据类型
     * @return 结果对象
     */
    public static <T> R<T> ok(T data) {
        return new R<T>()
                .setCode(ResultCode.SUCCESS.getCode())
                .setMessage(ResultCode.SUCCESS.getMessage())
                .setData(data)
                .setSuccess(true);
    }

    /**
     * 成功返回结果
     *
     * @param message 提示信息
     * @param <T>     数据类型
     * @return 结果对象
     */
    public static <T> R<T> ok(String message) {
        return new R<T>()
                .setCode(ResultCode.SUCCESS.getCode())
                .setMessage(message)
                .setSuccess(true);
    }

    /**
     * 成功返回结果
     *
     * @param data    返回数据
     * @param message 提示信息
     * @param <T>     数据类型
     * @return 结果对象
     */
    public static <T> R<T> ok(T data, String message) {
        return new R<T>()
                .setCode(ResultCode.SUCCESS.getCode())
                .setMessage(message)
                .setData(data)
                .setSuccess(true);
    }

    /**
     * 失败返回结果
     *
     * @param <T> 数据类型
     * @return 结果对象
     */
    public static <T> R<T> fail() {
        return new R<T>()
                .setCode(ResultCode.FAILURE.getCode())
                .setMessage(ResultCode.FAILURE.getMessage())
                .setSuccess(false);
    }

    /**
     * 失败返回结果
     *
     * @param message 提示信息
     * @param <T>     数据类型
     * @return 结果对象
     */
    public static <T> R<T> fail(String message) {
        return new R<T>()
                .setCode(ResultCode.FAILURE.getCode())
                .setMessage(message)
                .setSuccess(false);
    }

    /**
     * 失败返回结果
     *
     * @param code    状态码
     * @param message 提示信息
     * @param <T>     数据类型
     * @return 结果对象
     */
    public static <T> R<T> fail(Integer code, String message) {
        return new R<T>()
                .setCode(code)
                .setMessage(message)
                .setSuccess(false);
    }

    /**
     * 失败返回结果
     *
     * @param resultCode 返回码枚举
     * @param <T>        数据类型
     * @return 结果对象
     */
    public static <T> R<T> fail(IResultCode resultCode) {
        return new R<T>()
                .setCode(resultCode.getCode())
                .setMessage(resultCode.getMessage())
                .setSuccess(false);
    }

    /**
     * 失败返回结果
     *
     * @param resultCode 返回码枚举
     * @param message    提示信息
     * @param <T>        数据类型
     * @return 结果对象
     */
    public static <T> R<T> fail(IResultCode resultCode, String message) {
        return new R<T>()
                .setCode(resultCode.getCode())
                .setMessage(message)
                .setSuccess(false);
    }
} 