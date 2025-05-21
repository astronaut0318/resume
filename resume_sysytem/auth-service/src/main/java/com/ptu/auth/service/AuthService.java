package com.ptu.auth.service;

import com.ptu.auth.dto.ChangePasswordDTO;
import com.ptu.auth.dto.LoginDTO;
import com.ptu.auth.dto.RefreshTokenDTO;
import com.ptu.auth.vo.TokenVO;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 登录
     *
     * @param loginDTO 登录参数
     * @return 令牌信息
     */
    TokenVO login(LoginDTO loginDTO);

    /**
     * 刷新令牌
     *
     * @param refreshTokenDTO 刷新令牌参数
     * @return 令牌信息
     */
    TokenVO refresh(RefreshTokenDTO refreshTokenDTO);

    /**
     * 登出
     *
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean logout(Long userId);

    /**
     * 修改密码
     *
     * @param userId           用户ID
     * @param changePasswordDTO 修改密码参数
     * @return 是否成功
     */
    boolean changePassword(Long userId, ChangePasswordDTO changePasswordDTO);
} 