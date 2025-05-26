package com.ptu.ai.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简历分析请求DTO
 */
@Data
public class ResumeAnalyzeRequestDTO implements Serializable {
    /** 简历ID，必填 */
    @NotNull(message = "简历ID不能为空")
    private Long resumeId;
} 