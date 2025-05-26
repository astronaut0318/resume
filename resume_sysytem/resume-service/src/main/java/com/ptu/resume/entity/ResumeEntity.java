package com.ptu.resume.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 简历实体类
 */
@Data
@TableName("resumes")
public class ResumeEntity implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 简历标题
     */
    private String title;
    
    /**
     * 模板ID
     */
    private Long templateId;
    
    /**
     * 简历内容（JSON格式）
     */
    private String content;
    
    /**
     * 状态：0-草稿，1-已完成
     */
    private Integer status;
    
    /**
     * 是否默认简历：0-否，1-是
     */
    private Integer isDefault;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    /**
     * 逻辑删除：0-未删除，1-已删除
     */
    @TableLogic
    private Integer deleted;
} 