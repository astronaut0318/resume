package com.ptu.template.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ptu.template.common.constants.RedisKeyConstants;
import com.ptu.template.entity.TemplateCollectionEntity;
import com.ptu.template.mapper.TemplateCollectionMapper;
import com.ptu.template.service.TemplateCollectionService;
import com.ptu.common.util.RedisUtils;
import com.ptu.template.vo.TemplateCollectionVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 模板收藏服务实现类
 */
@Slf4j
@Service
public class TemplateCollectionServiceImpl extends ServiceImpl<TemplateCollectionMapper, TemplateCollectionEntity>
        implements TemplateCollectionService {

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public IPage<TemplateCollectionVO> pageUserCollections(Long userId, Integer page, Integer size) {
        // 参数校验
        if (userId == null) {
            return new Page<>();
        }
        
        // 设置默认参数
        page = page != null && page > 0 ? page : 1;
        size = size != null && size > 0 ? size : 10;
        
        // 创建分页对象
        Page<TemplateCollectionVO> pageParam = new Page<>(page, size);
        
        // 查询用户收藏的模板列表
        return baseMapper.selectUserCollectionPage(pageParam, userId);
    }

    @Override
    public boolean checkCollected(Long userId, Long templateId) {
        // 参数校验
        if (userId == null || templateId == null) {
            return false;
        }
        
        // 尝试从缓存获取
        String cacheKey = RedisKeyConstants.USER_COLLECTION_PREFIX + userId + ":" + templateId;
        Object cacheValue = redisUtils.get(cacheKey);
        if (cacheValue != null) {
            return (Boolean) cacheValue;
        }
        
        // 缓存未命中，从数据库查询
        LambdaQueryWrapper<TemplateCollectionEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TemplateCollectionEntity::getUserId, userId)
                    .eq(TemplateCollectionEntity::getTemplateId, templateId);
        boolean collected = count(queryWrapper) > 0;
        
        // 放入缓存，有效期1小时
        redisUtils.set(cacheKey, collected, TimeUnit.HOURS.toSeconds(1));
        
        return collected;
    }
} 