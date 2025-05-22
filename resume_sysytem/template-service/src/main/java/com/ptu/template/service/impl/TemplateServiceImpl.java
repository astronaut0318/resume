package com.ptu.template.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ptu.template.common.constants.RedisKeyConstants;
import com.ptu.template.common.query.TemplateQuery;
import com.ptu.template.entity.TemplateEntity;
import com.ptu.template.entity.TemplateCollectionEntity;
import com.ptu.template.mapper.TemplateMapper;
import com.ptu.template.service.TemplateCollectionService;
import com.ptu.template.service.TemplateService;
import com.ptu.common.util.RedisUtils;
import com.ptu.template.vo.ShareLinkVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 模板服务实现类
 */
@Slf4j
@Service
public class TemplateServiceImpl extends ServiceImpl<TemplateMapper, TemplateEntity> implements TemplateService {

    @Autowired
    private TemplateCollectionService templateCollectionService;

    @Autowired
    private RedisUtils redisUtils;

    @Value("${application.domain:http://localhost:8000}")
    private String domain;

    @Override
    public IPage<TemplateEntity> pageTemplates(TemplateQuery query) {
        Page<TemplateEntity> page = new Page<>(query.getPage(), query.getSize());
        return baseMapper.selectTemplatePage(page, query.getCategoryId(), query.getIsFree(), query.getKeyword());
    }

    @Override
    public TemplateEntity getTemplateDetail(Long id) {
        if (id == null) {
            return null;
        }

        // 尝试从缓存获取
        String cacheKey = RedisKeyConstants.TEMPLATE_DETAIL_PREFIX + id;
        Object cacheValue = redisUtils.get(cacheKey);
        if (cacheValue != null) {
            return (TemplateEntity) cacheValue;
        }

        // 缓存未命中，从数据库查询
        TemplateEntity template = getById(id);
        
        // 放入缓存，有效期1小时
        if (template != null) {
            redisUtils.set(cacheKey, template, TimeUnit.HOURS.toSeconds(1));
        }
        
        return template;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean collectTemplate(Long templateId, Long userId) {
        // 检查模板是否存在
        TemplateEntity template = getById(templateId);
        if (template == null) {
            return false;
        }

        // 检查是否已收藏
        if (templateCollectionService.checkCollected(userId, templateId)) {
            // 已收藏，无需重复操作
            return true;
        }

        // 创建收藏记录
        TemplateCollectionEntity collection = new TemplateCollectionEntity();
        collection.setUserId(userId);
        collection.setTemplateId(templateId);
        
        return templateCollectionService.save(collection);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelCollect(Long templateId, Long userId) {
        LambdaQueryWrapper<TemplateCollectionEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TemplateCollectionEntity::getUserId, userId)
                    .eq(TemplateCollectionEntity::getTemplateId, templateId);
        
        return templateCollectionService.remove(queryWrapper);
    }

    @Override
    public ShareLinkVO generateShareLink(Long templateId) {
        // 检查模板是否存在
        TemplateEntity template = getById(templateId);
        if (template == null) {
            return null;
        }

        // 生成唯一的分享码
        String shareCode = UUID.randomUUID().toString().replace("-", "");
        
        // 设置过期时间（7天后）
        LocalDateTime expireTime = LocalDateTime.now().plusDays(7);
        
        // 将模板ID和分享码的映射存入Redis，设置7天过期
        String cacheKey = RedisKeyConstants.TEMPLATE_SHARE_PREFIX + shareCode;
        redisUtils.set(cacheKey, templateId, TimeUnit.DAYS.toSeconds(7));
        
        // 构建分享链接视图对象
        ShareLinkVO shareLink = new ShareLinkVO();
        shareLink.setShareUrl(domain + "/share/template?code=" + shareCode);
        shareLink.setExpireTime(expireTime);
        
        return shareLink;
    }

    @Override
    public boolean incrementDownloads(Long templateId) {
        if (templateId == null) {
            return false;
        }

        // 原子性地增加下载次数，使用SQL的方式确保并发安全
        LambdaUpdateWrapper<TemplateEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(TemplateEntity::getId, templateId)
                    .setSql("downloads = downloads + 1");
        
        return update(updateWrapper);
    }
} 