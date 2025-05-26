package com.ptu.document.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ptu.document.entity.DocumentMetadataEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 文档元数据Mapper接口
 */
public interface DocumentMetadataMapper extends BaseMapper<DocumentMetadataEntity> {
    
    /**
     * 根据来源类型和ID获取文档元数据
     *
     * @param sourceType 来源类型
     * @param sourceId 来源ID
     * @return 文档元数据
     */
    @Select("SELECT * FROM document_metadata WHERE source_type = #{sourceType} AND source_id = #{sourceId} LIMIT 1")
    DocumentMetadataEntity getBySourceTypeAndSourceId(@Param("sourceType") String sourceType, @Param("sourceId") Long sourceId);
} 