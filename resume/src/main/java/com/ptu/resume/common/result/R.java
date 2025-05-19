package com.ptu.resume.common.result;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一返回结果
 *
 * @param <T> 数据类型
 */
@Data
public class R<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    private int code;

    /**
     * 返回消息
     */
    private String msg;

    /**
     * 返回数据
     */
    private T data;

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 成功返回结果
     */
    public static <T> R<T> ok() {
        return ok(null);
    }

    /**
     * 成功返回结果
     *
     * @param data 返回数据
     */
    public static <T> R<T> ok(T data) {
        return ok(data, "操作成功");
    }

    /**
     * 成功返回结果
     *
     * @param data 返回数据
     * @param msg  返回消息
     */
    public static <T> R<T> ok(T data, String msg) {
        R<T> r = new R<>();
        r.setCode(ResultCode.SUCCESS);
        r.setData(data);
        r.setMsg(msg);
        r.setSuccess(true);
        return r;
    }

    /**
     * 失败返回结果
     */
    public static <T> R<T> error() {
        return error(ResultCode.ERROR, "操作失败");
    }

    /**
     * 失败返回结果
     *
     * @param msg 返回消息
     */
    public static <T> R<T> error(String msg) {
        return error(ResultCode.ERROR, msg);
    }

    /**
     * 失败返回结果
     *
     * @param code 错误码
     * @param msg  返回消息
     */
    public static <T> R<T> error(int code, String msg) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setMsg(msg);
        r.setSuccess(false);
        return r;
    }

    /**
     * 失败返回结果
     *
     * @param errorCode 错误码
     */
    public static <T> R<T> error(IErrorCode errorCode) {
        return error(errorCode.getCode(), errorCode.getMsg());
    }
}