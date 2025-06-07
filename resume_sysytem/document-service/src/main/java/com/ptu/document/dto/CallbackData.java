package com.ptu.document.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.util.StdConverter;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * OnlyOffice回调数据DTO
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CallbackData implements Serializable {

    private static final long serialVersionUID = 1L;
    
    /**
     * 文档key
     */
    @JsonProperty("key")
    private String key;
    
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
     * 用户信息 - 通用处理器，兼容字符串和数组格式
     */
    @JsonProperty("users")
    @JsonDeserialize(using = UsersDeserializer.class)
    private List<String> users = new ArrayList<>();
    
    /**
     * 动作 - 通用处理器，兼容字符串和数组格式
     */
    @JsonProperty("actions")
    @JsonDeserialize(using = ActionsDeserializer.class)
    private List<Action> actions = new ArrayList<>();
    
    /**
     * 用户信息类
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
    
    /**
     * 操作信息类
     */
    @Data
    public static class Action {
        /**
         * 操作类型
         * 0 - 打开文档
         * 1 - 保存文档
         */
        @JsonProperty("type")
        private Integer type;
        
        /**
         * 用户ID
         */
        @JsonProperty("userid")
        private String userId;
    }
} 