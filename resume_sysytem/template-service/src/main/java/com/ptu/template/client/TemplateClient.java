package com.ptu.template.client;

import com.ptu.template.dto.TemplateDTO;
import java.util.List;

/**
 * 模板服务客户端接口
 * 提供模板相关的服务方法
 */
public interface TemplateClient {
    
    /**
     * 根据模板ID获取模板详情
     * @param templateId 模板ID
     * @return 模板详情DTO
     */
    TemplateDTO getTemplateById(Long templateId);
    
    /**
     * 批量获取模板信息
     * @param templateIds 模板ID列表
     * @return 模板详情DTO列表
     */
    List<TemplateDTO> getTemplatesByIds(List<Long> templateIds);
} 