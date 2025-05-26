package com.ptu.resume.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 简历视图对象
 */
@Data
public class ResumeVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 简历ID
     */
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
    private Object content;
    
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
} 