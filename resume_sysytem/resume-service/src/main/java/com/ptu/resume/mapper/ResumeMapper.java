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
    
    /**
     * 分页查询用户的简历列表
     *
     * @param page 分页参数
     * @param userId 用户ID
     * @return 简历列表
     */
    @Select("SELECT id, user_id, title, template_id, status, is_default, create_time, update_time FROM resumes WHERE user_id = #{userId} AND deleted = 0 ORDER BY is_default DESC, update_time DESC")
    IPage<ResumeEntity> pageByUserId(Page<ResumeEntity> page, @Param("userId") Long userId);
    
    /**
     * 获取用户的默认简历
     *
     * @param userId 用户ID
     * @return 默认简历
     */
    @Select("SELECT * FROM resumes WHERE user_id = #{userId} AND is_default = 1 AND deleted = 0 LIMIT 1")
    ResumeEntity getDefaultByUserId(@Param("userId") Long userId);
    
    /**
     * 重置用户所有简历为非默认
     *
     * @param userId 用户ID
     * @return 影响行数
     */
    @Update("UPDATE resumes SET is_default = 0 WHERE user_id = #{userId} AND deleted = 0")
    int resetDefault(@Param("userId") Long userId);
    
    /**
     * 设置指定简历为默认
     *
     * @param id 简历ID
     * @return 影响行数
     */
    @Update("UPDATE resumes SET is_default = 1 WHERE id = #{id} AND deleted = 0")
    int setDefault(@Param("id") Long id);
} 