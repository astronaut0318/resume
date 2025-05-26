package com.ptu.resume.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ptu.resume.dto.ResumeDTO;
import com.ptu.resume.entity.ResumeEntity;
import com.ptu.resume.vo.ResumeVO;
import com.ptu.resume.vo.ResumeVersionVO;

import java.util.List;

/**
 * 简历服务接口
 */
public interface ResumeService extends IService<ResumeEntity> {
    
    /**
     * 创建简历
     *
     * @param resumeDTO 简历数据
     * @return 简历ID
     */
    Long createResume(ResumeDTO resumeDTO);
    
    /**
     * 分页查询用户的简历列表
     *
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 简历列表
     */
    IPage<ResumeVO> pageUserResumes(Long userId, Integer page, Integer size);
    
    /**
     * 获取简历详情
     *
     * @param id 简历ID
     * @return 简历详情
     */
    ResumeVO getResumeDetail(Long id);
    
    /**
     * 获取用户默认简历
     *
     * @param userId 用户ID
     * @return 默认简历
     */
    ResumeVO getUserDefaultResume(Long userId);
    
    /**
     * 更新简历
     *
     * @param resumeDTO 简历数据
     * @return 是否成功
     */
    boolean updateResume(ResumeDTO resumeDTO);
    
    /**
     * 删除简历
     *
     * @param id 简历ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean deleteResume(Long id, Long userId);
    
    /**
     * 设置默认简历
     *
     * @param id 简历ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean setDefaultResume(Long id, Long userId);
    
    /**
     * 创建简历版本
     *
     * @param resumeId 简历ID
     * @return 版本号
     */
    Integer createResumeVersion(Long resumeId);
    
    /**
     * 获取简历版本列表
     *
     * @param resumeId 简历ID
     * @return 版本列表
     */
    List<ResumeVersionVO> listResumeVersions(Long resumeId);
    
    /**
     * 获取指定版本的简历内容
     *
     * @param resumeId 简历ID
     * @param version 版本号
     * @return 简历版本
     */
    ResumeVersionVO getResumeVersion(Long resumeId, Integer version);
} 