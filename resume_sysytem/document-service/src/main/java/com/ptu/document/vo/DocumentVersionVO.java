package com.ptu.document.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文档版本VO
 */
@Data
public class DocumentVersionVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 版本ID
     */
    private Long id;

    /**
     * 文档ID
     */
    private Long documentId;

    /**
     * 来源类型
     */
    private String sourceType;

    /**
     * 来源ID
     */
    private Long sourceId;

    /**
     * 版本号
     */
    private Integer version;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 修改者ID
     */
    private Long modifierId;

    /**
     * 修改者名称
     */
    private String modifierName;

    /**
     * 变更摘要
     */
    private String changeSummary;

    /**
     * 预览URL
     */
    private String previewUrl;

    /**
     * 是否当前版本
     */
    private Boolean isCurrent;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
} 