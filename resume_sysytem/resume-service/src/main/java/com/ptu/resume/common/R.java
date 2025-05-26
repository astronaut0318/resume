package com.ptu.resume.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应对象
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
     * 成功返回结果
     *
     * @param <T> 数据类型
     * @return 响应对象
     */
    public static <T> R<T> ok() {
        return ok(null);
    }
    
    /**
     * 成功返回结果
     *
     * @param data 数据
     * @param <T> 数据类型
     * @return 响应对象
     */
    public static <T> R<T> ok(T data) {
        return ok(data, "操作成功");
    }
    
    /**
     * 成功返回结果
     *
     * @param data 数据
     * @param message 消息
     * @param <T> 数据类型
     * @return 响应对象
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
     * 失败返回结果
     *
     * @param <T> 数据类型
     * @return 响应对象
     */
    public static <T> R<T> failed() {
        return failed("操作失败");
    }
    
    /**
     * 失败返回结果
     *
     * @param message 消息
     * @param <T> 数据类型
     * @return 响应对象
     */
    public static <T> R<T> failed(String message) {
        return failed(500, message);
    }
    
    /**
     * 失败返回结果
     *
     * @param code 状态码
     * @param message 消息
     * @param <T> 数据类型
     * @return 响应对象
     */
    public static <T> R<T> failed(int code, String message) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setMessage(message);
        r.setSuccess(false);
        return r;
    }
    
    /**
     * 参数验证失败返回结果
     *
     * @param <T> 数据类型
     * @return 响应对象
     */
    public static <T> R<T> badRequest() {
        return badRequest("参数不合法");
    }
    
    /**
     * 参数验证失败返回结果
     *
     * @param message 消息
     * @param <T> 数据类型
     * @return 响应对象
     */
    public static <T> R<T> badRequest(String message) {
        return failed(400, message);
    }
    
    /**
     * 未授权返回结果
     *
     * @param <T> 数据类型
     * @return 响应对象
     */
    public static <T> R<T> unauthorized() {
        return unauthorized("暂未登录或token已经过期");
    }
    
    /**
     * 未授权返回结果
     *
     * @param message 消息
     * @param <T> 数据类型
     * @return 响应对象
     */
    public static <T> R<T> unauthorized(String message) {
        return failed(401, message);
    }
    
    /**
     * 禁止访问返回结果
     *
     * @param <T> 数据类型
     * @return 响应对象
     */
    public static <T> R<T> forbidden() {
        return forbidden("没有相关权限");
    }
    
    /**
     * 禁止访问返回结果
     *
     * @param message 消息
     * @param <T> 数据类型
     * @return 响应对象
     */
    public static <T> R<T> forbidden(String message) {
        return failed(403, message);
    }
    
    /**
     * 资源不存在返回结果
     *
     * @param <T> 数据类型
     * @return 响应对象
     */
    public static <T> R<T> notFound() {
        return notFound("资源不存在");
    }
    
    /**
     * 资源不存在返回结果
     *
     * @param message 消息
     * @param <T> 数据类型
     * @return 响应对象
     */
    public static <T> R<T> notFound(String message) {
        return failed(404, message);
    }
    
    /**
     * 服务器错误返回结果
     *
     * @param <T> 数据类型
     * @return 响应对象
     */
    public static <T> R<T> serverError() {
        return serverError("服务器内部错误");
    }
    
    /**
     * 服务器错误返回结果
     *
     * @param message 消息
     * @param <T> 数据类型
     * @return 响应对象
     */
    public static <T> R<T> serverError(String message) {
        return failed(500, message);
    }
} 