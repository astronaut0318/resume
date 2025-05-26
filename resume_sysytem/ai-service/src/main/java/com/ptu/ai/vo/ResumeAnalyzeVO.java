package com.ptu.ai.vo;

import lombok.Data;
import java.io.Serializable;
import java.util.Map;

/**
 * 简历分析响应VO
 */
@Data
public class ResumeAnalyzeVO implements Serializable {
    /** 分数 */
    private Integer score;
    /** 分析详情 */
    private Map<String, String> analysis;
}
