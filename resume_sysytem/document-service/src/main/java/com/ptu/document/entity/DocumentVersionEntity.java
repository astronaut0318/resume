package com.ptu.document.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文档版本实体类
 */
@Data
@TableName("document_version")
public class DocumentVersionEntity implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
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
     * 文件大小(字节)
     */
    private Long fileSize;
    
    /**
     * 存储路径
     */
    @com.baomidou.mybatisplus.annotation.TableField("storage_path")
    private String storagePath;
    
    /**
     * 修改者ID
     */
    private Long userId;
    
    /**
     * 修改者名称
     */
    private String userName;
    
    /**
     * 变更摘要
     */
    private String changeSummary;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
} 