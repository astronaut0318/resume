package com.ptu.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ptu.common.exception.BusinessException;
import com.ptu.common.util.StringUtils;
import com.ptu.user.dto.RegisterDTO;
import com.ptu.user.entity.UserEntity;
import com.ptu.user.entity.UserDetailEntity;
import com.ptu.user.entity.UserVipEntity;
import com.ptu.user.mapper.UserMapper;
import com.ptu.user.service.UserService;
import com.ptu.user.service.UserDetailService;
import com.ptu.user.service.UserVipService;
import com.ptu.user.vo.RegisterVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {

    @Lazy
    @Autowired
    private UserDetailService userDetailService;
    @Lazy
    @Autowired
    private UserVipService userVipService;

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

    /**
     * 用户注册
     *
     * @param registerDTO 注册信息
     * @return 注册成功的用户信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public RegisterVO register(RegisterDTO registerDTO) {
        // 检查用户名是否已存在
        if (getByUsername(registerDTO.getUsername()) != null) {
            throw new BusinessException("用户名已存在");
        }

        // 检查手机号是否已存在
        if (getByPhone(registerDTO.getPhone()) != null) {
            throw new BusinessException("手机号已被注册");
        }

        // 检查邮箱是否已存在
        if (getByEmail(registerDTO.getEmail()) != null) {
            throw new BusinessException("邮箱已被注册");
        }

        // 创建用户实体
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(registerDTO.getUsername())
                .setPhone(registerDTO.getPhone())
                .setEmail(registerDTO.getEmail())
                // 使用MD5加密密码
                .setPassword(DigestUtils.md5DigestAsHex(registerDTO.getPassword().getBytes(StandardCharsets.UTF_8)))
                .setRole(0) // 0-普通用户
                .setStatus(1) // 1-正常
                .setDeleted(0) // 0-未删除
                .setCreateTime(LocalDateTime.now())
                .setUpdateTime(LocalDateTime.now());

        // 保存用户
        save(userEntity);
        log.info("用户注册成功：{}", userEntity.getUsername());

        // 注册时自动插入用户详情
        UserDetailEntity detail = new UserDetailEntity();
        detail.setUserId(userEntity.getId());
        detail.setCreateTime(LocalDateTime.now());
        detail.setUpdateTime(LocalDateTime.now());
        detail.setDeleted(0);
        userDetailService.save(detail);

        // 注册时可选插入VIP信息（如不想默认开通VIP可注释掉）
        // UserVipEntity vip = new UserVipEntity();
        // vip.setUserId(userEntity.getId());
        // vip.setLevel(0); // 默认VIP等级
        // vip.setStartTime(LocalDateTime.now());
        // vip.setEndTime(LocalDateTime.now().plusMonths(1)); // 默认1个月有效期
        // vip.setCreateTime(LocalDateTime.now());
        // vip.setUpdateTime(LocalDateTime.now());
        // vip.setDeleted(0);
        // userVipService.save(vip);

        // 返回注册结果
        return new RegisterVO()
                .setUserId(userEntity.getId())
                .setUsername(userEntity.getUsername());
    }
    
    /**
     * 验证用户密码
     *
     * @param userId   用户ID
     * @param password 密码（明文）
     * @return 是否匹配
     */
    @Override
    public boolean verifyPassword(Long userId, String password) {
        if (userId == null || StringUtils.isBlank(password)) {
            return false;
        }
        UserEntity user = getById(userId);
        if (user == null) {
            return false;
        }
        // 加密密码进行比较
        String encryptedPassword = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));
        return encryptedPassword.equals(user.getPassword());
    }
    
    /**
     * 更新用户密码
     *
     * @param userId      用户ID
     * @param newPassword 新密码（明文）
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePassword(Long userId, String newPassword) {
        return resetPassword(userId, newPassword);
    }
} 