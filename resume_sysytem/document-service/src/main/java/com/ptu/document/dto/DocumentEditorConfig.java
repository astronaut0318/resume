package com.ptu.document.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

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
         * 用户ID
         */
        private String user;
        
        /**
         * 用户名称
         */
        private String userName;
        
        /**
         * 回调URL
         */
        private String callbackUrl;
        
        /**
         * 构造方法
         *
         * @param mode 模式
         * @param user 用户ID
         * @param userName 用户名称
         */
        public EditorConfig(String mode, String user, String userName) {
            this.mode = mode;
            this.user = user;
            this.userName = userName;
        }
    }
} 