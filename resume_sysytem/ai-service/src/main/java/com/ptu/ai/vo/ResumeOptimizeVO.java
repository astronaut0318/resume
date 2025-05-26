package com.ptu.ai.vo;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * 内容优化建议响应VO
 */
@Data
public class ResumeOptimizeVO implements Serializable {
    /** 优化建议列表 */
    private List<Suggestion> suggestions;

    @Data
    public static class Suggestion implements Serializable {
        private String section;
        private Integer item;
        private String original;
        private String suggestion;
    }
} 