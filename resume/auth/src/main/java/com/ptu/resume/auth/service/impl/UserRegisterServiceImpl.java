package com.ptu.resume.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ptu.resume.auth.dto.UserRegisterDTO;
import com.ptu.resume.auth.entity.User;
import com.ptu.resume.auth.entity.UserRole;
import com.ptu.resume.auth.mapper.UserMapper;
import com.ptu.resume.auth.mapper.UserRoleMapper;
import com.ptu.resume.auth.service.UserRegisterService;
import com.ptu.resume.auth.service.UserService;
import com.ptu.resume.auth.vo.UserRegisterVO;
import com.ptu.resume.core.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 用户注册服务实现类
 * 严格按照API文档规范设计
 *
 * @author PTU
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserRegisterServiceImpl implements UserRegisterService {

    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * 用户注册
     *
     * @param userRegisterDTO 注册信息
     * @return 注册结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserRegisterVO register(UserRegisterDTO userRegisterDTO) {
        // 检查用户名是否可用
        if (!checkUsernameAvailable(userRegisterDTO.getUsername())) {
            throw new BusinessException("用户名已存在");
        }
        
        // 检查手机号是否可用（如果提供）
        if (StringUtils.hasText(userRegisterDTO.getPhone()) && 
            !checkPhoneAvailable(userRegisterDTO.getPhone())) {
            throw new BusinessException("手机号已存在");
        }
        
        // 检查邮箱是否可用（如果提供）
        if (StringUtils.hasText(userRegisterDTO.getEmail()) && 
            !checkEmailAvailable(userRegisterDTO.getEmail())) {
            throw new BusinessException("邮箱已存在");
        }
        
        // 创建用户
        User user = new User();
        user.setUsername(userRegisterDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userRegisterDTO.getPassword()));
        user.setEmail(userRegisterDTO.getEmail());
        user.setPhone(userRegisterDTO.getPhone());
        user.setNickname(userRegisterDTO.getUsername()); // 默认使用用户名作为昵称
        user.setStatus(0); // 正常状态
        user.setIsDeleted(0); // 未删除
        
        // 保存用户
        userService.save(user);
        
        // 分配默认角色（普通用户角色）
        UserRole userRole = new UserRole();
        userRole.setUserId(user.getId());
        userRole.setRoleId(2L); // 假设2为普通用户角色ID
        userRoleMapper.insert(userRole);
        
        log.info("用户注册成功: 用户名[{}], 用户ID[{}]", user.getUsername(), user.getId());
        
        // 返回注册结果
        return new UserRegisterVO(user.getId(), user.getUsername());
    }

    /**
     * 检查用户名是否可用
     *
     * @param username 用户名
     * @return 是否可用
     */
    @Override
    public boolean checkUsernameAvailable(String username) {
        User user = userService.getByUsername(username);
        return user == null;
    }

    /**
     * 检查手机号是否可用
     *
     * @param phone 手机号
     * @return 是否可用
     */
    @Override
    public boolean checkPhoneAvailable(String phone) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, phone);
        return userService.count(wrapper) == 0;
    }

    /**
     * 检查邮箱是否可用
     *
     * @param email 邮箱
     * @return 是否可用
     */
    @Override
    public boolean checkEmailAvailable(String email) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, email);
        return userService.count(wrapper) == 0;
    }
} 