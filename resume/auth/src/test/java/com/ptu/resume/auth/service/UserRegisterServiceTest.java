package com.ptu.resume.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ptu.resume.auth.dto.UserRegisterDTO;
import com.ptu.resume.auth.entity.User;
import com.ptu.resume.auth.entity.UserRole;
import com.ptu.resume.auth.mapper.UserMapper;
import com.ptu.resume.auth.mapper.UserRoleMapper;
import com.ptu.resume.auth.service.impl.UserRegisterServiceImpl;
import com.ptu.resume.auth.vo.UserRegisterVO;
import com.ptu.resume.core.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * 用户注册服务测试类
 *
 * @author PTU
 */
@ExtendWith(MockitoExtension.class)
public class UserRegisterServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRoleMapper userRoleMapper;

    @Mock
    private UserService userService;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserRegisterServiceImpl userRegisterService;

    private UserRegisterDTO userRegisterDTO;

    @BeforeEach
    public void setUp() {
        userRegisterDTO = new UserRegisterDTO();
        userRegisterDTO.setUsername("testuser");
        userRegisterDTO.setPassword("password123");
        userRegisterDTO.setEmail("test@example.com");
        userRegisterDTO.setPhone("13800138000");
    }

    @Test
    public void testRegisterSuccess() {
        // 模拟服务方法返回
        when(userService.getByUsername(anyString())).thenReturn(null);
        when(userService.count(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(passwordEncoder.encode(anyString())).thenReturn("encrypted_password");
        
        // 模拟保存用户
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername(userRegisterDTO.getUsername());
        
        doAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return null;
        }).when(userService).save(any(User.class));
        
        // 执行注册
        UserRegisterVO result = userRegisterService.register(userRegisterDTO);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals("testuser", result.getUsername());
        
        // 验证方法调用
        verify(userService, times(1)).save(any(User.class));
        verify(userRoleMapper, times(1)).insert(any(UserRole.class));
    }

    @Test
    public void testRegisterWithExistingUsername() {
        // 模拟用户名已存在
        User existingUser = new User();
        existingUser.setId(2L);
        existingUser.setUsername("testuser");
        
        when(userService.getByUsername("testuser")).thenReturn(existingUser);
        
        // 执行注册并验证异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userRegisterService.register(userRegisterDTO);
        });
        
        assertEquals("用户名已存在", exception.getMessage());
        
        // 验证不应该保存用户
        verify(userService, never()).save(any(User.class));
    }

    @Test
    public void testRegisterWithExistingPhone() {
        // 模拟用户名不存在，但手机号已存在
        when(userService.getByUsername(anyString())).thenReturn(null);
        when(userService.count(any(LambdaQueryWrapper.class))).thenReturn(1L);
        
        // 执行注册并验证异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userRegisterService.register(userRegisterDTO);
        });
        
        assertEquals("手机号已存在", exception.getMessage());
        
        // 验证不应该保存用户
        verify(userService, never()).save(any(User.class));
    }

    @Test
    public void testCheckUsernameAvailable() {
        // 模拟用户名不存在
        when(userService.getByUsername("available")).thenReturn(null);
        
        // 模拟用户名已存在
        User existingUser = new User();
        existingUser.setUsername("unavailable");
        when(userService.getByUsername("unavailable")).thenReturn(existingUser);
        
        // 验证结果
        assertTrue(userRegisterService.checkUsernameAvailable("available"));
        assertFalse(userRegisterService.checkUsernameAvailable("unavailable"));
    }
} 