package com.ptu.template.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ptu.template.common.constants.RedisKeyConstants;
import com.ptu.template.entity.TemplateCategoryEntity;
import com.ptu.template.mapper.TemplateCategoryMapper;
import com.ptu.template.service.TemplateCategoryService;
import com.ptu.common.util.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 模板分类服务实现类
 */
@Slf4j
@Service
public class TemplateCategoryServiceImpl extends ServiceImpl<TemplateCategoryMapper, TemplateCategoryEntity>
        implements TemplateCategoryService {

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public List<TemplateCategoryEntity> listCategories() {
        // 尝试从缓存获取
        Object cacheValue = redisUtils.get(RedisKeyConstants.TEMPLATE_CATEGORY_LIST);
        if (cacheValue != null) {
            return (List<TemplateCategoryEntity>) cacheValue;
        }

        // 缓存未命中，从数据库查询
        LambdaQueryWrapper<TemplateCategoryEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TemplateCategoryEntity::getStatus, 1)
                    .orderByAsc(TemplateCategoryEntity::getSort);
        List<TemplateCategoryEntity> categories = list(queryWrapper);

        // 放入缓存，有效期1天
        if (categories != null && !categories.isEmpty()) {
            redisUtils.set(RedisKeyConstants.TEMPLATE_CATEGORY_LIST, categories, TimeUnit.DAYS.toSeconds(1));
        }

        return categories;
    }
} 