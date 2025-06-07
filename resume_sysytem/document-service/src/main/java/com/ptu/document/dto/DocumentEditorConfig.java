package com.ptu.document.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 文档编辑器配置DTO
 */
@Data
public class DocumentEditorConfig implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 文档类型：word/cell/slide
     */
    private String documentType;
    
    /**
     * 文档信息
     */
    private Document document;
    
    /**
     * 编辑器配置
     */
    private EditorConfig editorConfig;
    
    /**
     * 回调URL
     */
    private String callbackUrl;
    
    /**
     * 令牌
     */
    private String token;
    
    /**
     * 文档服务器地址
     */
    private String docserviceApiUrl;
    
    /**
     * 文档信息
     */
    @Data
    @NoArgsConstructor
    public static class Document {
        /**
         * 文件名
         */
        private String title;
        
        /**
         * 文件URL
         */
        private String url;
        
        /**
         * 文件类型
         */
        private String fileType;
        
        /**
         * 密钥
         */
        private String key;

        /**
         * 文件信息
         */
        private Map<String, Object> info;
        
        /**
         * 权限
         */
        private Map<String, Object> permissions;
        
        /**
         * 构造方法
         *
         * @param title 文件名
         * @param url 文件URL
         * @param fileType 文件类型
         */
        public Document(String title, String url, String fileType) {
            this.title = title;
            this.url = url;
            this.fileType = fileType;
            this.key = String.valueOf(System.currentTimeMillis());
            this.info = new HashMap<>();
            this.permissions = new HashMap<>();
        }
    }
    
    /**
     * 编辑器配置
     */
    @Data
    @NoArgsConstructor
    public static class EditorConfig {
        /**
         * 模式：view/edit/comment
         */
        private String mode;
        
        /**
         * 语言
         */
        private String lang = "zh-CN";
        
        /**
         * 用户信息
         */
        private Map<String, Object> user;
        
        /**
         * 回调URL
         */
        private String callbackUrl;
        
        /**
         * 自定义配置
         */
        private Map<String, Object> customization;
        
        /**
         * 嵌入配置
         */
        private Map<String, Object> embedded;

        /**
         * 权限
         */
        private Map<String, Object> permissions;
        
        /**
         * 统计配置
         */
        private Map<String, Object> coEditing;
        
        /**
         * 保存配置
         */
        private Map<String, Object> plugins;
        
        /**
         * 构造方法
         *
         * @param mode 模式
         * @param userId 用户ID
         * @param userName 用户名称
         */
        public EditorConfig(String mode, String userId, String userName) {
            this.mode = mode;
            
            // 设置用户信息
            this.user = new HashMap<>();
            this.user.put("id", userId);
            this.user.put("name", userName != null ? userName : ("用户" + userId));
            
            // 设置自定义配置
            this.customization = new HashMap<>();
            this.customization.put("chat", true);
            this.customization.put("comments", true);
            this.customization.put("compactHeader", false);
            this.customization.put("compactToolbar", false);
            this.customization.put("feedback", false);
            this.customization.put("forcesave", true);
            this.customization.put("help", true);
            this.customization.put("hideRightMenu", false);
            this.customization.put("mentionShare", true);
            this.customization.put("plugins", true);
            this.customization.put("showReviewChanges", false);
            this.customization.put("toolbarNoTabs", false);
            
            // 设置权限
            this.permissions = new HashMap<>();
            if ("view".equals(mode)) {
                this.permissions.put("comment", false);
                this.permissions.put("download", true);
                this.permissions.put("edit", false);
                this.permissions.put("print", true);
                this.permissions.put("review", false);
                this.permissions.put("fillForms", false);
                this.permissions.put("modifyFilter", false);
                this.permissions.put("modifyContentControl", false);
            } else if ("edit".equals(mode)) {
                this.permissions.put("comment", true);
                this.permissions.put("download", true);
                this.permissions.put("edit", true);
                this.permissions.put("print", true);
                this.permissions.put("review", true);
                this.permissions.put("fillForms", true);
                this.permissions.put("modifyFilter", true);
                this.permissions.put("modifyContentControl", true);
            } else if ("comment".equals(mode)) {
                this.permissions.put("comment", true);
                this.permissions.put("download", true);
                this.permissions.put("edit", false);
                this.permissions.put("print", true);
                this.permissions.put("review", false);
                this.permissions.put("fillForms", false);
                this.permissions.put("modifyFilter", false);
                this.permissions.put("modifyContentControl", false);
            }
            
            // 协作编辑配置
            this.coEditing = new HashMap<>();
            this.coEditing.put("mode", "fast");
            this.coEditing.put("change", true);
            
            // 插件配置
            this.plugins = new HashMap<>();
        }
    }
} 