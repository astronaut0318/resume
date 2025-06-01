package com.ptu.resume.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ptu.resume.entity.ResumeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 简历Mapper接口
 */
@Mapper
public interface ResumeMapper extends BaseMapper<ResumeEntity> {
    // ... existing code ...

    /**
     * 分页查询所有简历列表
     * @param page 分页参数
     * @return 简历列表
     */
    @Select("SELECT id, user_id, title, template_id, status, is_default, create_time, update_time FROM resumes WHERE deleted = 0 ORDER BY update_time DESC")
    IPage<ResumeEntity> pageAllResumes(Page<ResumeEntity> page);
}
