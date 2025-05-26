package com.ptu.resume.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ptu.common.exception.BusinessException;
import com.ptu.common.utils.PageUtils;
import com.ptu.resume.dto.ResumeDTO;
import com.ptu.resume.entity.ResumeEntity;
import com.ptu.resume.entity.ResumeVersionEntity;
import com.ptu.resume.mapper.ResumeMapper;
import com.ptu.resume.mapper.ResumeVersionMapper;
import com.ptu.resume.service.ResumeService;
import com.ptu.resume.vo.ResumeVO;
import com.ptu.resume.vo.ResumeVersionVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 简历服务实现类
 */
@Service("resumeService")
public class ResumeServiceImpl extends ServiceImpl<ResumeMapper, ResumeEntity> implements ResumeService {

    @Autowired
    private ResumeVersionMapper resumeVersionMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createResume(ResumeDTO resumeDTO) {
        // 参数校验
        if (resumeDTO.getUserId() == null) {
            throw new BusinessException("用户ID不能为空");
        }

        // DTO转实体
        ResumeEntity resumeEntity = new ResumeEntity();
        BeanUtils.copyProperties(resumeDTO, resumeEntity);

        // 设置初始状态
        if (resumeEntity.getStatus() == null) {
            resumeEntity.setStatus(0); // 默认草稿状态
        }
        if (resumeEntity.getIsDefault() == null) {
            resumeEntity.setIsDefault(0); // 默认非默认简历
        }

        // 如果设置为默认简历，则将其他简历设为非默认
        if (resumeEntity.getIsDefault() == 1) {
            baseMapper.resetDefault(resumeDTO.getUserId());
        }

        // 保存简历
        save(resumeEntity);

        // 创建首个版本
        if (resumeEntity.getContent() != null) {
            createInitialVersion(resumeEntity.getId(), resumeEntity.getContent());
        }

        return resumeEntity.getId();
    }

    @Override
    public IPage<ResumeVO> pageUserResumes(Long userId, Integer page, Integer size) {
        // 参数校验
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }

        // 分页查询
        Page<ResumeEntity> pageParam = new Page<>(page, size);
        IPage<ResumeEntity> pageResult = baseMapper.pageByUserId(pageParam, userId);

        // 转换为VO
        return PageUtils.convertPage(pageResult, ResumeVO.class);
    }

    @Override
    public ResumeVO getResumeDetail(Long id) {
        // 参数校验
        if (id == null) {
            throw new BusinessException("简历ID不能为空");
        }

        // 获取简历
        ResumeEntity resumeEntity = getById(id);
        if (resumeEntity == null) {
            throw new BusinessException("简历不存在");
        }

        // 转换为VO
        ResumeVO resumeVO = new ResumeVO();
        BeanUtils.copyProperties(resumeEntity, resumeVO);
        
        return resumeVO;
    }

    @Override
    public ResumeVO getUserDefaultResume(Long userId) {
        // 参数校验
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }

        // 获取默认简历
        ResumeEntity resumeEntity = baseMapper.getDefaultByUserId(userId);
        if (resumeEntity == null) {
            return null;
        }

        // 转换为VO
        ResumeVO resumeVO = new ResumeVO();
        BeanUtils.copyProperties(resumeEntity, resumeVO);
        
        return resumeVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateResume(ResumeDTO resumeDTO) {
        // 参数校验
        if (resumeDTO.getId() == null) {
            throw new BusinessException("简历ID不能为空");
        }

        // 获取原简历
        ResumeEntity oldEntity = getById(resumeDTO.getId());
        if (oldEntity == null) {
            throw new BusinessException("简历不存在");
        }

        // 权限校验
        if (!Objects.equals(oldEntity.getUserId(), resumeDTO.getUserId())) {
            throw new BusinessException("无权限操作此简历");
        }

        // 如果内容有变化，创建新版本
        if (resumeDTO.getContent() != null && !resumeDTO.getContent().equals(oldEntity.getContent())) {
            createResumeVersion(resumeDTO.getId());
        }

        // DTO转实体
        ResumeEntity resumeEntity = new ResumeEntity();
        BeanUtils.copyProperties(resumeDTO, resumeEntity);

        // 如果设置为默认简历，则将其他简历设为非默认
        if (resumeEntity.getIsDefault() != null && resumeEntity.getIsDefault() == 1) {
            baseMapper.resetDefault(oldEntity.getUserId());
        }

        // 更新简历
        return updateById(resumeEntity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteResume(Long id, Long userId) {
        // 参数校验
        if (id == null) {
            throw new BusinessException("简历ID不能为空");
        }
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }

        // 获取简历
        ResumeEntity resumeEntity = getById(id);
        if (resumeEntity == null) {
            throw new BusinessException("简历不存在");
        }

        // 权限校验
        if (!Objects.equals(resumeEntity.getUserId(), userId)) {
            throw new BusinessException("无权限操作此简历");
        }

        // 删除简历
        return removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean setDefaultResume(Long id, Long userId) {
        // 参数校验
        if (id == null) {
            throw new BusinessException("简历ID不能为空");
        }
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }

        // 获取简历
        ResumeEntity resumeEntity = getById(id);
        if (resumeEntity == null) {
            throw new BusinessException("简历不存在");
        }

        // 权限校验
        if (!Objects.equals(resumeEntity.getUserId(), userId)) {
            throw new BusinessException("无权限操作此简历");
        }

        // 重置用户所有简历为非默认
        baseMapper.resetDefault(userId);

        // 设置当前简历为默认
        return baseMapper.setDefault(id) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer createResumeVersion(Long resumeId) {
        // 参数校验
        if (resumeId == null) {
            throw new BusinessException("简历ID不能为空");
        }

        // 获取简历
        ResumeEntity resumeEntity = getById(resumeId);
        if (resumeEntity == null) {
            throw new BusinessException("简历不存在");
        }

        // 创建版本
        return createVersion(resumeId, resumeEntity.getContent());
    }

    @Override
    public List<ResumeVersionVO> listResumeVersions(Long resumeId) {
        // 参数校验
        if (resumeId == null) {
            throw new BusinessException("简历ID不能为空");
        }

        // 获取版本列表
        List<ResumeVersionEntity> entities = resumeVersionMapper.listByResumeId(resumeId);
        
        // 转换为VO
        List<ResumeVersionVO> vos = new ArrayList<>(entities.size());
        for (ResumeVersionEntity entity : entities) {
            ResumeVersionVO vo = new ResumeVersionVO();
            BeanUtils.copyProperties(entity, vo);
            vos.add(vo);
        }
        
        return vos;
    }

    @Override
    public ResumeVersionVO getResumeVersion(Long resumeId, Integer version) {
        // 参数校验
        if (resumeId == null) {
            throw new BusinessException("简历ID不能为空");
        }
        if (version == null) {
            throw new BusinessException("版本号不能为空");
        }

        // 获取版本
        ResumeVersionEntity entity = resumeVersionMapper.getByVersion(resumeId, version);
        if (entity == null) {
            throw new BusinessException("版本不存在");
        }

        // 转换为VO
        ResumeVersionVO vo = new ResumeVersionVO();
        BeanUtils.copyProperties(entity, vo);
        
        return vo;
    }

    /**
     * 创建初始版本
     *
     * @param resumeId 简历ID
     * @param content 简历内容
     * @return 版本号
     */
    private Integer createInitialVersion(Long resumeId, String content) {
        return createVersion(resumeId, content);
    }

    /**
     * 创建版本
     *
     * @param resumeId 简历ID
     * @param content 简历内容
     * @return 版本号
     */
    private Integer createVersion(Long resumeId, String content) {
        // 获取最新版本号
        Integer latestVersion = resumeVersionMapper.getLatestVersion(resumeId);
        int newVersion = latestVersion == null ? 1 : latestVersion + 1;

        // 创建版本实体
        ResumeVersionEntity versionEntity = new ResumeVersionEntity();
        versionEntity.setResumeId(resumeId);
        versionEntity.setVersion(newVersion);
        versionEntity.setContent(content);

        // 保存版本
        resumeVersionMapper.insert(versionEntity);

        return newVersion;
    }
} 