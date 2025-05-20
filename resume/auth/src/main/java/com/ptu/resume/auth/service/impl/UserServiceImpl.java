package com.ptu.resume.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ptu.resume.auth.dto.LoginDTO;
import com.ptu.resume.auth.dto.RegisterDTO;
import com.ptu.resume.auth.entity.Permission;
import com.ptu.resume.auth.entity.Role;
import com.ptu.resume.auth.entity.User;
import com.ptu.resume.auth.entity.UserRole;
import com.ptu.resume.auth.mapper.PermissionMapper;
import com.ptu.resume.auth.mapper.RoleMapper;
import com.ptu.resume.auth.mapper.UserMapper;
import com.ptu.resume.auth.mapper.UserRoleMapper;
import com.ptu.resume.auth.service.UserService;
import com.ptu.resume.auth.util.JwtUtil;
import com.ptu.resume.auth.vo.RoleVO;
import com.ptu.resume.auth.vo.TokenVO;
import com.ptu.resume.auth.vo.UserVO;
import com.ptu.resume.core.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 *
 * @author PTU
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;
    private final UserRoleMapper userRoleMapper;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;
    private final BCryptPasswordEncoder passwordEncoder;

    private static final String TOKEN_PREFIX = "auth:token:";
    private static final String USER_PREFIX = "auth:user:";
    private static final long TOKEN_EXPIRE = 7 * 24 * 60 * 60; // 7天

    /**
     * 用户登录
     *
     * @param loginDTO 登录信息
     * @return 令牌信息
     */
    @Override
    public TokenVO login(LoginDTO loginDTO) {
        // 查询用户
        User user = getByUsername(loginDTO.getUsername());
        if (user == null) {
            log.warn("用户登录失败: 用户名[{}]不存在", loginDTO.getUsername());
            throw new BusinessException("用户名或密码错误");
        }
        
        // 验证密码
        boolean matches = passwordEncoder.matches(loginDTO.getPassword(), user.getPassword());
        log.info("密码验证结果: 用户名[{}], 输入密码[{}], 存储密码哈希[{}], 匹配结果[{}]", 
                loginDTO.getUsername(), loginDTO.getPassword(), user.getPassword(), matches);
        
        if (!matches) {
            log.warn("用户登录失败: 用户名[{}]密码错误", loginDTO.getUsername());
            throw new BusinessException("用户名或密码错误");
        }
        
        // 验证账号状态
        if (user.getStatus() != 0) {
            log.warn("用户登录失败: 用户名[{}]账号已被禁用", loginDTO.getUsername());
            throw new BusinessException("账号已被禁用");
        }

        // 转换为UserVO
        UserVO userVO = convertToUserVO(user);
        
        // 生成令牌
        String accessToken = jwtUtil.generateAccessToken(userVO);
        String refreshToken = jwtUtil.generateRefreshToken(userVO);

        // 存储令牌和用户信息到Redis
        String tokenKey = TOKEN_PREFIX + user.getId();
        String userKey = USER_PREFIX + user.getId();
        redisTemplate.opsForValue().set(tokenKey, accessToken, TOKEN_EXPIRE, TimeUnit.SECONDS);
        redisTemplate.opsForValue().set(userKey, userVO, TOKEN_EXPIRE, TimeUnit.SECONDS);

        log.info("用户登录成功: 用户名[{}]", loginDTO.getUsername());
        // 构建并返回令牌信息
        return new TokenVO(accessToken, refreshToken, TOKEN_EXPIRE, "Bearer", userVO);
    }

    /**
     * 用户注册
     *
     * @param registerDTO 注册信息
     * @return 用户ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long register(RegisterDTO registerDTO) {
        // 验证两次密码是否一致
        if (!Objects.equals(registerDTO.getPassword(), registerDTO.getConfirmPassword())) {
            throw new BusinessException("两次密码不一致");
        }

        // 验证用户名是否已存在
        User existUser = getByUsername(registerDTO.getUsername());
        if (existUser != null) {
            throw new BusinessException("用户名已存在");
        }
        
        // 验证邮箱是否已存在
        LambdaQueryWrapper<User> emailWrapper = new LambdaQueryWrapper<>();
        emailWrapper.eq(User::getEmail, registerDTO.getEmail());
        long emailCount = count(emailWrapper);
        if (emailCount > 0) {
            throw new BusinessException("邮箱已存在");
        }
        
        // 创建用户
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setNickname(registerDTO.getNickname());
        user.setPhone(registerDTO.getPhone());
        user.setEmail(registerDTO.getEmail());
        user.setStatus(0); // 正常状态
        user.setIsDeleted(0); // 未删除
        
        // 保存用户
        save(user);
        
        // 分配默认角色
        UserRole userRole = new UserRole();
        userRole.setUserId(user.getId());
        userRole.setRoleId(2L); // 假设2为普通用户角色ID
        userRoleMapper.insert(userRole);
        
        return user.getId();
    }

    /**
     * 刷新令牌
     *
     * @param refreshToken 刷新令牌
     * @return 新的令牌信息
     */
    @Override
    public TokenVO refreshToken(String refreshToken) {
        // 验证刷新令牌
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new BusinessException("刷新令牌已过期");
        }
        
        // 获取用户ID
        Long userId = jwtUtil.getUserIdFromToken(refreshToken);
        User user = getById(userId);
        if (user == null || user.getStatus() != 0) {
            throw new BusinessException("用户不存在或已被禁用");
        }
        
        // 转换为UserVO
        UserVO userVO = convertToUserVO(user);

        // 生成新令牌
        String accessToken = jwtUtil.generateAccessToken(userVO);
        String newRefreshToken = jwtUtil.generateRefreshToken(userVO);
        
        // 更新Redis中的令牌
        String tokenKey = TOKEN_PREFIX + userId;
        String userKey = USER_PREFIX + userId;
        redisTemplate.opsForValue().set(tokenKey, accessToken, TOKEN_EXPIRE, TimeUnit.SECONDS);
        redisTemplate.opsForValue().set(userKey, userVO, TOKEN_EXPIRE, TimeUnit.SECONDS);

        // 构建并返回令牌信息
        return new TokenVO(accessToken, newRefreshToken, TOKEN_EXPIRE, "Bearer", userVO);
    }

    /**
     * 根据用户ID获取用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    @Override
    public UserVO getUserById(Long userId) {
        User user = getById(userId);
        if (user == null) {
            return null;
        }
        return convertToUserVO(user);
    }

    /**
     * 根据用户名获取用户信息
     *
     * @param username 用户名
     * @return 用户信息
     */
    @Override
    public User getByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    /**
     * 获取用户角色编码列表
     *
     * @param userId 用户ID
     * @return 角色编码列表
     */
    @Override
    public List<String> getRoleCodes(Long userId) {
        List<Role> roles = roleMapper.selectRolesByUserId(userId);
        return roles.stream()
                .map(Role::getCode)
                .collect(Collectors.toList());
    }

    /**
     * 获取用户权限编码列表
     *
     * @param userId 用户ID
     * @return 权限编码列表
     */
    @Override
    public List<String> getPermissionCodes(Long userId) {
        List<Permission> permissions = permissionMapper.selectPermissionsByUserId(userId);
        return permissions.stream()
                .map(Permission::getCode)
                .collect(Collectors.toList());
    }

    /**
     * 将User转换为UserVO
     *
     * @param user 用户实体
     * @return 用户VO
     */
    private UserVO convertToUserVO(User user) {
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        
        // 获取角色列表
        List<Role> roles = roleMapper.selectRolesByUserId(user.getId());
        List<RoleVO> roleVOS = new ArrayList<>();
        for (Role role : roles) {
                RoleVO roleVO = new RoleVO();
            BeanUtils.copyProperties(role, roleVO);
            roleVOS.add(roleVO);
        }
        userVO.setRoles(roleVOS);
            
        // 获取权限编码列表
        List<String> permissions = getPermissionCodes(user.getId());
        userVO.setPermissions(permissions);
        
        return userVO;
    }
}