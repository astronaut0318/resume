package com.ptu.document.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ptu.document.entity.DocumentMetadataEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文档元数据数据访问接口
 */
@Mapper
public interface DocumentMetadataMapper extends BaseMapper<DocumentMetadataEntity> {
} 