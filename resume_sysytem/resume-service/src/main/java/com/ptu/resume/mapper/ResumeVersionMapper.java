package com.ptu.resume.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ptu.resume.entity.ResumeVersionEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 简历版本Mapper接口
 */
@Mapper
public interface ResumeVersionMapper extends BaseMapper<ResumeVersionEntity> {
    
    /**
     * 获取简历的最新版本号
     *
     * @param resumeId 简历ID
     * @return 版本号
     */
    @Select("SELECT MAX(version) FROM resume_versions WHERE resume_id = #{resumeId} AND deleted = 0")
    Integer getLatestVersion(@Param("resumeId") Long resumeId);
    
    /**
     * 获取简历版本列表
     *
     * @param resumeId 简历ID
     * @return 版本列表
     */
    @Select("SELECT id, resume_id, version, create_time FROM resume_versions WHERE resume_id = #{resumeId} AND deleted = 0 ORDER BY version DESC")
    List<ResumeVersionEntity> listByResumeId(@Param("resumeId") Long resumeId);
    
    /**
     * 获取指定版本的简历
     *
     * @param resumeId 简历ID
     * @param version 版本号
     * @return 简历版本
     */
    @Select("SELECT * FROM resume_versions WHERE resume_id = #{resumeId} AND version = #{version} AND deleted = 0 LIMIT 1")
    ResumeVersionEntity getByVersion(@Param("resumeId") Long resumeId, @Param("version") Integer version);
} 