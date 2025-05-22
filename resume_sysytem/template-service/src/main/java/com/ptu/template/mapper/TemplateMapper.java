package com.ptu.template.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ptu.template.entity.TemplateEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 模板Mapper
 */
@Mapper
public interface TemplateMapper extends BaseMapper<TemplateEntity> {

    /**
     * 分页查询模板列表，支持筛选条件
     *
     * @param page       分页参数
     * @param categoryId 分类ID
     * @param isFree     是否免费
     * @param keyword    搜索关键词
     * @return 分页结果
     */
    IPage<TemplateEntity> selectTemplatePage(Page<TemplateEntity> page,
                                     @Param("categoryId") Long categoryId,
                                     @Param("isFree") Integer isFree,
                                     @Param("keyword") String keyword);
}