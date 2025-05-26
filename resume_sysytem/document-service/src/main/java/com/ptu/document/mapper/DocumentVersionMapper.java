package com.ptu.document.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ptu.document.entity.DocumentVersionEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 文档版本Mapper接口
 */
@Mapper
public interface DocumentVersionMapper extends BaseMapper<DocumentVersionEntity> {

    /**
     * 获取最新版本号
     * 
     * @param sourceType 来源类型
     * @param sourceId 来源ID
     * @return 版本号
     */
    @Select("SELECT MAX(version) FROM document_version WHERE source_type = #{sourceType} AND source_id = #{sourceId}")
    Integer getLatestVersion(@Param("sourceType") String sourceType, @Param("sourceId") Long sourceId);
    
    /**
     * 获取版本列表
     * 
     * @param sourceType 来源类型
     * @param sourceId 来源ID
     * @return 版本列表
     */
    @Select("SELECT * FROM document_version WHERE source_type = #{sourceType} AND source_id = #{sourceId} ORDER BY version DESC")
    List<DocumentVersionEntity> getVersionList(@Param("sourceType") String sourceType, @Param("sourceId") Long sourceId);
    
    /**
     * 获取指定版本
     * 
     * @param sourceType 来源类型
     * @param sourceId 来源ID
     * @param version 版本号
     * @return 版本信息
     */
    @Select("SELECT * FROM doc_document_version WHERE source_type = #{sourceType} AND source_id = #{sourceId} AND version = #{version} AND deleted = 0")
    DocumentVersionEntity getVersion(@Param("sourceType") String sourceType,
                                  @Param("sourceId") Long sourceId,
                                  @Param("version") Integer version);
} 