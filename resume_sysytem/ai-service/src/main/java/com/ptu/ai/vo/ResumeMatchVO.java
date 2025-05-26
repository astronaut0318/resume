package com.ptu.ai.vo;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * 职位匹配分析响应VO
 */
@Data
public class ResumeMatchVO implements Serializable {
    /** 匹配分数 */
    private Integer matchScore;
    /** 分析说明 */
    private String analysis;
    /** 优势列表 */
    private List<String> advantages;
    /** 劣势列表 */
    private List<String> weaknesses;
    /** 建议列表 */
    private List<String> suggestions;
} 