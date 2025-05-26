package com.ptu.file.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

/**
 * 文件实体类
 * <p>
 * 对应数据库files表，存储模板、简历、头像三类文件的元数据。
 * </p>
 */
@Data
@TableName("files")
public class FileEntity {
    /**
     * 主键ID
     */
    @TableId
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 原始文件名
     */
    private String originalName;

    /**
     * 存储文件名
     */
    private String fileName;

    /**
     * 文件路径（MinIO或本地存储路径）
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
     * 业务ID（如模板ID、简历ID、用户ID，头像时为用户ID）
     */
    private Long bizId;

    /**
     * 文件扩展名
     */
    private String fileExt;

    /**
     * 创建时间
     */
    private Date createTime;
} 