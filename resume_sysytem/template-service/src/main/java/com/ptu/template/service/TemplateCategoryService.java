package com.ptu.template.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ptu.template.entity.TemplateCategoryEntity;

import java.util.List;

/**
 * 模板分类服务接口
 */
public interface TemplateCategoryService extends IService<TemplateCategoryEntity> {

    /**
     * 获取模板分类列表
     *
     * @return 模板分类列表
     */
    List<TemplateCategoryEntity> listCategories();
} 