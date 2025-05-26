package com.ptu.ai.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 职位匹配分析请求DTO
 */
@Data
public class ResumeMatchRequestDTO implements Serializable {
    /** 简历ID，必填 */
    @NotNull(message = "简历ID不能为空")
    private Long resumeId;

    /** 目标职位，必填 */
    @NotNull(message = "目标职位不能为空")
    private String position;

    /** 职位描述，必填 */
    @NotNull(message = "职位描述不能为空")
    private String jobDescription;
} 