package com.ptu.resume.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ptu.resume.auth.dto.LoginDTO;
import com.ptu.resume.auth.dto.RegisterDTO;
import com.ptu.resume.auth.entity.User;
import com.ptu.resume.auth.vo.TokenVO;
import com.ptu.resume.auth.vo.UserVO;

import java.util.List;

/**
 * 用户服务接口
 *
 * @author PTU
 */
public interface UserService extends IService<User> {

    /**
     * 用户登录
     *
     * @param loginDTO 登录信息
     * @return 令牌信息
     */
    TokenVO login(LoginDTO loginDTO);

    /**
     * 用户注册
     *
     * @param registerDTO 注册信息
     * @return 用户ID
     */
    Long register(RegisterDTO registerDTO);

    /**
     * 刷新令牌
     *
     * @param refreshToken 刷新令牌
     * @return 新的令牌信息
     */
    TokenVO refreshToken(String refreshToken);

    /**
     * 根据用户ID获取用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    UserVO getUserById(Long userId);

    /**
     * 根据用户名获取用户信息
     *
     * @param username 用户名
     * @return 用户信息
     */
    User getByUsername(String username);

    /**
     * 获取用户角色编码列表
     *
     * @param userId 用户ID
     * @return 角色编码列表
     */
    List<String> getRoleCodes(Long userId);

    /**
     * 获取用户权限编码列表
     *
     * @param userId 用户ID
     * @return 权限编码列表
     */
    List<String> getPermissionCodes(Long userId);
}