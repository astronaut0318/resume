package com.ptu.file.util;

import lombok.Data;
import java.io.Serializable;

/**
 * 统一响应对象
 * <p>
 * 用于API接口的统一返回格式，包含状态码、消息、数据等。
 * </p>
 * @param <T> 数据类型
 */
@Data
public class R<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 状态码 */
    private int code;
    /** 消息 */
    private String message;
    /** 数据 */
    private T data;
    /** 时间戳 */
    private long timestamp;
    /** 是否成功 */
    private boolean success;

    private R() {
        this.timestamp = System.currentTimeMillis();
    }

    /** 成功返回 */
    public static <T> R<T> ok() {
        return ok(null);
    }
    public static <T> R<T> ok(T data) {
        return ok(data, "操作成功");
    }
    public static <T> R<T> ok(T data, String message) {
        R<T> r = new R<>();
        r.setCode(200);
        r.setData(data);
        r.setMessage(message);
        r.setSuccess(true);
        return r;
    }
    /** 失败返回 */
    public static <T> R<T> fail() {
        return fail("操作失败");
    }
    public static <T> R<T> fail(String message) {
        return fail(500, message);
    }
    public static <T> R<T> fail(int code, String message) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setMessage(message);
        r.setSuccess(false);
        return r;
    }
} 