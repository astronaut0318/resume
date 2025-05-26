package com.ptu.ai.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简历内容优化建议请求DTO
 */
@Data
public class ResumeOptimizeRequestDTO implements Serializable {
    /** 简历ID，必填 */
    @NotNull(message = "简历ID不能为空")
    private Long resumeId;

    /** 优化的简历分区，可选 */
    private String section;
} 