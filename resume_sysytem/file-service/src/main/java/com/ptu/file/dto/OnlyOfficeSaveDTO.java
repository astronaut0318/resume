package com.ptu.file.dto;

import lombok.Data;

/**
 * OnlyOffice保存回调DTO
 * <p>
 * 用于OnlyOffice文档保存回调接口的参数封装。
 * </p>
 */
@Data
public class OnlyOfficeSaveDTO {
    /**
     * 文件ID
     */
    private Long fileId;

    /**
     * 文件类型（template/resume）
     */
    private String fileType;

    /**
     * 业务ID（如模板ID、简历ID）
     */
    private Long bizId;

    /**
     * OnlyOffice回传的最新文件下载地址
     */
    private String fileUrl;

    /**
     * 编辑用户ID
     */
    private Long userId;
} 