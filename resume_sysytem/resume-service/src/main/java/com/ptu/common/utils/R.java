package com.ptu.common.utils;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应结果
 * 
 * @param <T> 数据类型
 */
@Data
public class R<T> implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 状态码
     */
    private Integer code;
    
    /**
     * 消息
     */
    private String msg;
    
    /**
     * 数据
     */
    private T data;
    
    public static <T> R<T> ok() {
        return ok(null);
    }
    
    public static <T> R<T> ok(T data) {
        R<T> r = new R<>();
        r.setCode(0);
        r.setMsg("成功");
        r.setData(data);
        return r;
    }
    
    public static <T> R<T> error() {
        return error(500, "系统错误");
    }
    
    public static <T> R<T> error(String msg) {
        return error(500, msg);
    }
    
    public static <T> R<T> error(int code, String msg) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setMsg(msg);
        return r;
    }
} 