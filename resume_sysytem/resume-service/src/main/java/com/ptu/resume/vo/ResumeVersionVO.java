package com.ptu.resume.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 简历版本视图对象
 */
@Data
public class ResumeVersionVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 版本ID
     */
    private Long id;
    
    /**
     * 简历ID
     */
    private Long resumeId;
    
    /**
     * 版本号
     */
    private Integer version;
    
    /**
     * 简历内容（JSON格式）
     */
    private Object content;
    
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
} 