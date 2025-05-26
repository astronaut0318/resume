package com.ptu.resume.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简历数据传输对象
 */
@Data
public class ResumeDTO implements Serializable {
    
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
    @NotBlank(message = "简历标题不能为空")
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
    @NotNull(message = "状态不能为空")
    private Integer status;
    
    /**
     * 是否设为默认简历
     */
    private Boolean isDefault;
} 