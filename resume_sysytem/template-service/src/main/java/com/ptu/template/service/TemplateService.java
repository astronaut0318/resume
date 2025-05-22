package com.ptu.template.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ptu.template.common.query.TemplateQuery;
import com.ptu.template.entity.TemplateEntity;
import com.ptu.template.vo.ShareLinkVO;

/**
 * 模板服务接口
 */
public interface TemplateService extends IService<TemplateEntity> {

    /**
     * 分页查询模板列表
     *
     * @param query 查询条件
     * @return 分页结果
     */
    IPage<TemplateEntity> pageTemplates(TemplateQuery query);

    /**
     * 获取模板详情
     *
     * @param id 模板ID
     * @return 模板详情
     */
    TemplateEntity getTemplateDetail(Long id);

    /**
     * 收藏模板
     *
     * @param templateId 模板ID
     * @param userId     用户ID
     * @return 是否成功
     */
    boolean collectTemplate(Long templateId, Long userId);

    /**
     * 取消收藏
     *
     * @param templateId 模板ID
     * @param userId     用户ID
     * @return 是否成功
     */
    boolean cancelCollect(Long templateId, Long userId);

    /**
     * 生成模板分享链接
     *
     * @param templateId 模板ID
     * @return 分享信息
     */
    ShareLinkVO generateShareLink(Long templateId);

    /**
     * 增加下载次数
     *
     * @param templateId 模板ID
     * @return 是否成功
     */
    boolean incrementDownloads(Long templateId);
} 