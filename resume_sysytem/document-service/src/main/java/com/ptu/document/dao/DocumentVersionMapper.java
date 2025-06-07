package com.ptu.document.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ptu.document.entity.DocumentVersionEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 文档版本数据访问接口
 */
@Mapper
public interface DocumentVersionMapper extends BaseMapper<DocumentVersionEntity> {
    
    /**
     * 获取最大版本号
     *
     * @param sourceType 来源类型
     * @param sourceId   来源ID
     * @return 最大版本号
     */
    @Select("SELECT MAX(version) FROM document_versions WHERE source_type = #{sourceType} AND source_id = #{sourceId}")
    Integer getMaxVersionNumber(@Param("sourceType") String sourceType, @Param("sourceId") Long sourceId);
} 