package com.ptu.file.vo;

import lombok.Data;

/**
 * 文件上传响应VO
 * <p>
 * 用于文件上传接口的返回结果，包含文件的关键信息。
 * </p>
 */
@Data
public class FileUploadVO {
    /**
     * 文件ID
     */
    private Long fileId;

    /**
     * 存储文件名
     */
    private String fileName;

    /**
     * 文件访问路径（URL）
     */
    private String filePath;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 文件类型（template/resume/avatar）
     */
    private String fileType;
    
    /**
     * 文件完整访问URL
     */
    private String fileUrl;
} 