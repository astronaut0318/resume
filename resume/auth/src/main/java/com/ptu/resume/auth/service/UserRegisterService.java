package com.ptu.resume.auth.service;

import com.ptu.resume.auth.dto.UserRegisterDTO;
import com.ptu.resume.auth.vo.UserRegisterVO;

/**
 * 用户注册服务接口
 * 严格按照API文档规范设计
 *
 * @author PTU
 */
public interface UserRegisterService {

    /**
     * 用户注册
     *
     * @param userRegisterDTO 注册信息
     * @return 注册结果
     */
    UserRegisterVO register(UserRegisterDTO userRegisterDTO);

    /**
     * 检查用户名是否可用
     *
     * @param username 用户名
     * @return 是否可用
     */
    boolean checkUsernameAvailable(String username);

    /**
     * 检查手机号是否可用
     *
     * @param phone 手机号
     * @return 是否可用
     */
    boolean checkPhoneAvailable(String phone);

    /**
     * 检查邮箱是否可用
     *
     * @param email 邮箱
     * @return 是否可用
     */
    boolean checkEmailAvailable(String email);
} 