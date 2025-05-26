package com.ptu.ai.service.impl;

import com.ptu.ai.dto.*;
import com.ptu.ai.vo.*;
import com.ptu.ai.service.ResumeAIService;
import com.ptu.ai.feign.ResumeFeignClient;
import com.ptu.common.api.R;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.io.IOException;

/**
 * 简历AI服务实现类，封装deepseek-V3 API调用
 */
@Slf4j
@Service
public class ResumeAIServiceImpl implements ResumeAIService {

    @Resource
    private ResumeFeignClient resumeFeignClient;

    @Value("${ai.deepseek.api-url}")
    private String aiApiUrl;
    @Value("${ai.deepseek.api-key}")
    private String aiApiKey;
    @Value("${ai.deepseek.timeout:30s}")
    private String aiTimeout;

    private final OkHttpClient httpClient = new OkHttpClient();

    @Override
    public ResumeAnalyzeVO analyzeResume(ResumeAnalyzeRequestDTO dto, Long userId) {
        // 1. 通过Feign获取简历内容
        R<Object> resumeResp = resumeFeignClient.getResumeDetail(dto.getResumeId());
        // 2. 组装AI请求，调用deepseek-V3
        // 3. 解析AI响应，组装VO
        // TODO: 真实实现
        return new ResumeAnalyzeVO();
    }

    @Override
    public ResumeOptimizeVO optimizeResume(ResumeOptimizeRequestDTO dto, Long userId) {
        // TODO: 真实实现
        return new ResumeOptimizeVO();
    }

    @Override
    public ResumeMatchVO matchResume(ResumeMatchRequestDTO dto, Long userId) {
        // TODO: 真实实现
        return new ResumeMatchVO();
    }

    @Override
    public ResumeGenerateVO generateResumeContent(ResumeGenerateRequestDTO dto, Long userId) {
        // TODO: 真实实现
        return new ResumeGenerateVO();
    }

    // deepseek-V3 API调用封装（可根据需要扩展）
    private String callDeepseek(String prompt) throws IOException {
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), prompt);
        Request request = new Request.Builder()
                .url(aiApiUrl)
                .addHeader("Authorization", "Bearer " + aiApiKey)
                .post(body)
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("AI接口调用失败: " + response);
            }
            return response.body() != null ? response.body().string() : null;
        }
    }
} 