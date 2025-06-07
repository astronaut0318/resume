package com.ptu.file.vo;

import lombok.Data;
import java.util.Date;

/**
 * 文件VO
 * <p>
 * 用于文件列表、详情接口的返回结果，包含文件的详细信息。
 * </p>
 */
@Data
public class FileVO {
    /**
     * 文件ID
     */
    private Long id;

    /**
     * 原始文件名
     */
    private String originalName;

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
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 文件完整访问URL
     */
    private String fileUrl;
} 