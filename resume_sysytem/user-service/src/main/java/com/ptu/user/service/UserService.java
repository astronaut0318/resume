package com.ptu.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ptu.user.entity.UserEntity;

/**
 * 用户服务接口
 */
public interface UserService extends IService<UserEntity> {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户实体
     */
    UserEntity getByUsername(String username);

    /**
     * 根据手机号查询用户
     *
     * @param phone 手机号
     * @return 用户实体
     */
    UserEntity getByPhone(String phone);

    /**
     * 根据邮箱查询用户
     *
     * @param email 邮箱
     * @return 用户实体
     */
    UserEntity getByEmail(String email);

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
    IPage<UserEntity> pageUsers(int page, int size, String username, String phone, String email);

    /**
     * 修改用户状态
     *
     * @param userId 用户ID
     * @param status 状态：0-正常，1-禁用
     * @return 是否成功
     */
    boolean updateStatus(Long userId, Integer status);

    /**
     * 重置密码
     *
     * @param userId      用户ID
     * @param newPassword 新密码
     * @return 是否成功
     */
    boolean resetPassword(Long userId, String newPassword);
} 