package com.ptu.ai.controller;

import com.ptu.ai.dto.*;
import com.ptu.ai.vo.*;
import com.ptu.ai.service.ResumeAIService;
import com.ptu.common.api.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

/**
 * 简历AI接口控制器
 */
@RestController
@RequestMapping("/api/ai/resume")
public class ResumeAIController {

    @Autowired
    private ResumeAIService resumeAIService;

    /**
     * 简历分析
     */
    @PostMapping("/analyze")
    public R<ResumeAnalyzeVO> analyze(@Valid @RequestBody ResumeAnalyzeRequestDTO dto) {
        Long userId = getCurrentUserId();
        ResumeAnalyzeVO vo = resumeAIService.analyzeResume(dto, userId);
        return R.ok(vo);
    }

    /**
     * 内容优化建议
     */
    @PostMapping("/optimize")
    public R<ResumeOptimizeVO> optimize(@Valid @RequestBody ResumeOptimizeRequestDTO dto) {
        Long userId = getCurrentUserId();
        ResumeOptimizeVO vo = resumeAIService.optimizeResume(dto, userId);
        return R.ok(vo);
    }

    /**
     * 职位匹配分析
     */
    @PostMapping("/match")
    public R<ResumeMatchVO> match(@Valid @RequestBody ResumeMatchRequestDTO dto) {
        Long userId = getCurrentUserId();
        ResumeMatchVO vo = resumeAIService.matchResume(dto, userId);
        return R.ok(vo);
    }

    /**
     * 自动生成简历内容
     */
    @PostMapping("/generate")
    public R<ResumeGenerateVO> generate(@Valid @RequestBody ResumeGenerateRequestDTO dto) {
        Long userId = getCurrentUserId();
        ResumeGenerateVO vo = resumeAIService.generateResumeContent(dto, userId);
        return R.ok(vo);
    }

    /**
     * 获取当前登录用户ID（需集成JWT或网关鉴权，示例用1L）
     */
    private Long getCurrentUserId() {
        // TODO: 实现真实用户ID获取逻辑
        return 1L;
    }
} 