package com.ptu.document.dto;

import lombok.Data;
import java.io.Serializable;
import java.util.Map;

/**
 * 简历详情DTO（与resume-service返回结构保持一致，可根据实际字段调整）
 */
@Data
public class ResumeDetailDTO implements Serializable {
    private Long id;
    private Long userId;
    private String title;
    private Long templateId;
    private Map<String, Object> content; // 简历内容（JSON）
    private Integer status;
    private Integer isDefault;
    private String createTime;
    private String updateTime;
}
