package com.ptu.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ptu.user.dto.UserDetailUpdateDTO;
import com.ptu.user.entity.UserDetailEntity;
import com.ptu.user.entity.UserEntity;
import com.ptu.user.mapper.UserDetailMapper;
import com.ptu.user.service.UserDetailService;
import com.ptu.user.service.UserService;
import com.ptu.user.vo.UserDetailVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.annotation.Lazy;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

/**
 * 用户详细信息服务实现类
 */
@Slf4j
@Service
public class UserDetailServiceImpl extends ServiceImpl<UserDetailMapper, UserDetailEntity> implements UserDetailService {

    @Lazy
    @Autowired
    private UserService userService;

    /**
     * 根据用户ID获取用户详细信息
     *
     * @param userId 用户ID
     * @return 用户详细信息VO
     */
    @Override
    public UserDetailVO getDetailByUserId(Long userId) {
        // 校验用户是否存在
        UserEntity userEntity = userService.getById(userId);
        if (userEntity == null) {
            return null;
        }

        // 查询用户详细信息
        LambdaQueryWrapper<UserDetailEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserDetailEntity::getUserId, userId);
        UserDetailEntity detailEntity = getOne(queryWrapper);

        // 转换为VO返回
        if (detailEntity != null) {
            UserDetailVO detailVO = new UserDetailVO();
            BeanUtils.copyProperties(detailEntity, detailVO);
            return detailVO;
        }
        
        return null;
    }

    /**
     * 更新用户详细信息
     *
     * @param userId            用户ID
     * @param userDetailUpdateDTO 详细信息更新DTO
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDetail(Long userId, UserDetailUpdateDTO userDetailUpdateDTO) {
        // 校验用户是否存在
        UserEntity userEntity = userService.getById(userId);
        if (userEntity == null) {
            return false;
        }

        // 查询用户详细信息
        LambdaQueryWrapper<UserDetailEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserDetailEntity::getUserId, userId);
        UserDetailEntity detailEntity = getOne(queryWrapper);

        // 如果不存在，则创建一条新记录
        if (detailEntity == null) {
            return createDetail(userId, userDetailUpdateDTO);
        }

        // 更新详细信息
        BeanUtils.copyProperties(userDetailUpdateDTO, detailEntity);
        detailEntity.setUpdateTime(LocalDateTime.now());
        return updateById(detailEntity);
    }

    /**
     * 创建用户详细信息
     *
     * @param userId            用户ID
     * @param userDetailUpdateDTO 详细信息更新DTO
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createDetail(Long userId, UserDetailUpdateDTO userDetailUpdateDTO) {
        // 校验用户是否存在
        UserEntity userEntity = userService.getById(userId);
        if (userEntity == null) {
            return false;
        }

        // 查询是否已存在
        LambdaQueryWrapper<UserDetailEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserDetailEntity::getUserId, userId);
        UserDetailEntity existEntity = getOne(queryWrapper);
        if (existEntity != null) {
            return updateDetail(userId, userDetailUpdateDTO);
        }

        // 创建新的详细信息
        UserDetailEntity detailEntity = new UserDetailEntity();
        BeanUtils.copyProperties(userDetailUpdateDTO, detailEntity);
        detailEntity.setUserId(userId);
        detailEntity.setCreateTime(LocalDateTime.now());
        detailEntity.setUpdateTime(LocalDateTime.now());
        detailEntity.setDeleted(0);
        return save(detailEntity);
    }
} 