package com.ptu.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ptu.common.util.StringUtils;
import com.ptu.user.entity.UserEntity;
import com.ptu.user.mapper.UserMapper;
import com.ptu.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户实体
     */
    @Override
    public UserEntity getByUsername(String username) {
        if (StringUtils.isBlank(username)) {
            return null;
        }
        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserEntity::getUsername, username);
        return getOne(queryWrapper);
    }

    /**
     * 根据手机号查询用户
     *
     * @param phone 手机号
     * @return 用户实体
     */
    @Override
    public UserEntity getByPhone(String phone) {
        if (StringUtils.isBlank(phone)) {
            return null;
        }
        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserEntity::getPhone, phone);
        return getOne(queryWrapper);
    }

    /**
     * 根据邮箱查询用户
     *
     * @param email 邮箱
     * @return 用户实体
     */
    @Override
    public UserEntity getByEmail(String email) {
        if (StringUtils.isBlank(email)) {
            return null;
        }
        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserEntity::getEmail, email);
        return getOne(queryWrapper);
    }

    /**
     * 分页查询用户列表
     *
     * @param page     页码
     * @param size     每页大小
     * @param username 用户名（可选）
     * @param phone    手机号（可选）
     * @param email    邮箱（可选）
     * @return 分页用户列表
     */
    @Override
    public IPage<UserEntity> pageUsers(int page, int size, String username, String phone, String email) {
        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(username), UserEntity::getUsername, username)
                .like(StringUtils.isNotBlank(phone), UserEntity::getPhone, phone)
                .like(StringUtils.isNotBlank(email), UserEntity::getEmail, email)
                .orderByDesc(UserEntity::getCreateTime);
        return page(new Page<>(page, size), queryWrapper);
    }

    /**
     * 修改用户状态
     *
     * @param userId 用户ID
     * @param status 状态：0-正常，1-禁用
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(Long userId, Integer status) {
        if (userId == null || status == null) {
            return false;
        }
        UserEntity user = getById(userId);
        if (user == null) {
            return false;
        }
        user.setStatus(status);
        user.setUpdateTime(LocalDateTime.now());
        return updateById(user);
    }

    /**
     * 重置密码
     *
     * @param userId      用户ID
     * @param newPassword 新密码
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean resetPassword(Long userId, String newPassword) {
        if (userId == null || StringUtils.isBlank(newPassword)) {
            return false;
        }
        UserEntity user = getById(userId);
        if (user == null) {
            return false;
        }
        // 使用MD5加密密码
        String encryptedPassword = DigestUtils.md5DigestAsHex(newPassword.getBytes(StandardCharsets.UTF_8));
        user.setPassword(encryptedPassword);
        user.setUpdateTime(LocalDateTime.now());
        return updateById(user);
    }
} 