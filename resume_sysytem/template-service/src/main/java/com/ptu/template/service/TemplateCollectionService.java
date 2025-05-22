package com.ptu.template.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ptu.template.entity.TemplateCollectionEntity;
import com.ptu.template.vo.TemplateCollectionVO;

/**
 * 模板收藏服务接口
 */
public interface TemplateCollectionService extends IService<TemplateCollectionEntity> {

    /**
     * 获取用户收藏的模板列表
     *
     * @param userId 用户ID
     * @param page   页码
     * @param size   每页大小
     * @return 分页结果
     */
    IPage<TemplateCollectionVO> pageUserCollections(Long userId, Integer page, Integer size);

    /**
     * 检查用户是否已收藏模板
     *
     * @param userId     用户ID
     * @param templateId 模板ID
     * @return 是否已收藏
     */
    boolean checkCollected(Long userId, Long templateId);
} 