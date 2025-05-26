package com.ptu.document.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * OnlyOffice回调数据DTO
 */
@Data
public class CallbackData implements Serializable {

    private static final long serialVersionUID = 1L;
    
    /**
     * 动作（0-文档已准备就绪,1-文档已准备就绪（强制保存模式）,2-文档已保存,3-文档保存失败,4-文档已编辑）
     */
    @JsonProperty("status")
    private Integer status;
    
    /**
     * 文档URL
     */
    @JsonProperty("url")
    private String url;
    
    /**
     * 错误代码
     */
    @JsonProperty("error")
    private Integer error;
    
    /**
     * 错误描述
     */
    @JsonProperty("errorDescription")
    private String errorDescription;
    
    /**
     * 令牌
     */
    @JsonProperty("token")
    private String token;
    
    /**
     * 用户ID
     */
    @JsonProperty("users")
    private String users;
    
    /**
     * 动作
     */
    @JsonProperty("actions")
    private String actions;
    
    /**
     * 用户信息
     */
    @JsonProperty("users")
    private Users[] usersArray;
    
    /**
     * 用户信息
     */
    @Data
    public static class Users {
        /**
         * 用户ID
         */
        @JsonProperty("id")
        private String id;
        
        /**
         * 用户名称
         */
        @JsonProperty("name")
        private String name;
    }
} 