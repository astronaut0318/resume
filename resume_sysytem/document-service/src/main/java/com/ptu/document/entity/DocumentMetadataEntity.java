package com.ptu.document.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文档元数据实体类
 */
@Data
@TableName("document_metadata")
public class DocumentMetadataEntity implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 来源类型：RESUME-简历, TEMPLATE-模板, FILE-普通文件
     */
    private String sourceType;
    
    /**
     * 来源ID
     */
    private Long sourceId;
    
    /**
     * 文件名
     */
    private String fileName;
    
    /**
     * 文件扩展名
     */
    private String fileExt;
    
    /**
     * 文件大小(字节)
     */
    private Long fileSize;
    
    /**
     * 内容类型
     */
    private String contentType;
    
    /**
     * 存储路径
     */
    private String storagePath;
    
    /**
     * 当前版本号
     */
    private Integer version;
    
    /**
     * 创建者ID
     */
    private Long creatorId;
    
    /**
     * 最后修改者ID
     */
    private Long lastModifierId;
    
    /**
     * 最后修改时间
     */
    private LocalDateTime lastModified;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 