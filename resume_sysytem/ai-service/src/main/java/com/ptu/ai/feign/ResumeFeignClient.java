package com.ptu.ai.feign;

import com.ptu.common.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.stereotype.Component;

/**
 * 简历服务Feign客户端
 */
@FeignClient(name = "resume-service", fallbackFactory = ResumeFeignClient.ResumeFeignFallbackFactory.class)
public interface ResumeFeignClient {

    /**
     * 获取简历详情
     * @param resumeId 简历ID
     * @return 简历内容（JSON）
     */
    @GetMapping("/api/resumes/{resumeId}")
    R<Object> getResumeDetail(@PathVariable("resumeId") Long resumeId);

    @Component
    class ResumeFeignFallbackFactory implements org.springframework.cloud.openfeign.FallbackFactory<ResumeFeignClient> {
        @Override
        public ResumeFeignClient create(Throwable cause) {
            return resumeId -> R.failed("简历服务不可用");
        }
    }
} 