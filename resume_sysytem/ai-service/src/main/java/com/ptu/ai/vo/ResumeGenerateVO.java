package com.ptu.ai.vo;

import lombok.Data;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 自动生成简历内容响应VO
 */
@Data
public class ResumeGenerateVO implements Serializable {
    /** 个人简介 */
    private String profile;
    /** 工作经历 */
    private List<WorkExperience> workExperience;
    /** 项目经历 */
    private List<Project> projects;

    @Data
    public static class WorkExperience implements Serializable {
        private String company;
        private String position;
        private List<String> responsibilities;
    }

    @Data
    public static class Project implements Serializable {
        private String name;
        private String description;
        private List<String> highlights;
    }
} 