package com.ptu.template.controller;

import com.ptu.common.api.R;
import com.ptu.template.entity.TemplateCategoryEntity;
import com.ptu.template.service.TemplateCategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 模板分类控制器
 */
@Slf4j
@Api(tags = "模板分类接口")
@RestController
@RequestMapping("/templates/categories")
@RequiredArgsConstructor
public class TemplateCategoryController {

    @Autowired
    private TemplateCategoryService templateCategoryService;

    /**
     * 获取模板分类列表
     *
     * @return 分类列表
     */
    @GetMapping
    @ApiOperation("获取模板分类列表")
    public R<List<TemplateCategoryEntity>> listCategories() {
        List<TemplateCategoryEntity> categories = templateCategoryService.listCategories();
        return R.ok(categories);
    }
} 