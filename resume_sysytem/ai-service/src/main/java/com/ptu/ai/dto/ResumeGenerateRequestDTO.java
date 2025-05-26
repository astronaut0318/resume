package com.ptu.ai.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 自动生成简历内容请求DTO
 */
@Data
public class ResumeGenerateRequestDTO implements Serializable {
    /** 目标职位，必填 */
    @NotBlank(message = "目标职位不能为空")
    private String position;

    /** 行业，必填 */
    @NotBlank(message = "行业不能为空")
    private String industry;

    /** 工作年限，必填 */
    @NotNull(message = "工作年限不能为空")
    private Integer workYears;

    /** 关键技能，必填 */
    @NotNull(message = "关键技能不能为空")
    private List<String> keySkills;

    /** 教育信息，必填 */
    @NotNull(message = "教育信息不能为空")
    private EducationDTO education;

    @Data
    public static class EducationDTO implements Serializable {
        @NotBlank(message = "学校不能为空")
        private String school;
        @NotBlank(message = "专业不能为空")
        private String major;
        @NotBlank(message = "学历不能为空")
        private String degree;
        @NotNull(message = "毕业年份不能为空")
        private Integer graduationYear;
    }
} 