package com.ptu.document.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import com.ptu.document.dto.ResumeDetailDTO;

/**
 * 简历服务Feign客户端
 */
@FeignClient(name = "resume-service", url = "http://localhost:8081") // url可根据实际情况调整
public interface ResumeFeignClient {

    /**
     * 获取简历详情
     * @param resumeId 简历ID
     * @param userId 用户ID
     * @return 简历详情DTO
     */
    @GetMapping("/api/resumes/{resumeId}")
    ResumeDetailDTO getResumeDetail(@PathVariable("resumeId") Long resumeId, @RequestHeader("userId") Long userId);
}
