package com.ptu.ai.service;

import com.ptu.ai.dto.*;
import com.ptu.ai.vo.*;

/**
 * 简历AI服务接口
 */
public interface ResumeAIService {
    /**
     * 简历分析
     */
    ResumeAnalyzeVO analyzeResume(ResumeAnalyzeRequestDTO dto, Long userId);

    /**
     * 内容优化建议
     */
    ResumeOptimizeVO optimizeResume(ResumeOptimizeRequestDTO dto, Long userId);

    /**
     * 职位匹配分析
     */
    ResumeMatchVO matchResume(ResumeMatchRequestDTO dto, Long userId);

    /**
     * 自动生成简历内容
     */
    ResumeGenerateVO generateResumeContent(ResumeGenerateRequestDTO dto, Long userId);
} 