package com.ptu.template.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ptu.template.entity.TemplateCollectionEntity;
import com.ptu.template.vo.TemplateCollectionVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 模板收藏Mapper
 */
@Mapper
public interface TemplateCollectionMapper extends BaseMapper<TemplateCollectionEntity> {

    /**
     * 分页查询用户收藏的模板列表
     *
     * @param page   分页参数
     * @param userId 用户ID
     * @return 分页结果
     */
    IPage<TemplateCollectionVO> selectUserCollectionPage(Page<TemplateCollectionVO> page,
                                                        @Param("userId") Long userId);
} 