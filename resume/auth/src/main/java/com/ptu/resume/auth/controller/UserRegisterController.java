package com.ptu.resume.auth.controller;

import com.ptu.resume.auth.dto.UserRegisterDTO;
import com.ptu.resume.auth.service.UserRegisterService;
import com.ptu.resume.auth.vo.UserRegisterVO;
import com.ptu.resume.core.model.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 用户注册控制器
 * 严格按照API文档规范设计
 *
 * @author PTU
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "用户服务", description = "用户服务相关接口")
public class UserRegisterController {

    private final UserRegisterService userRegisterService;

    /**
     * 用户注册
     *
     * @param userRegisterDTO 注册信息
     * @return 注册结果
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "注册新用户")
    public R<UserRegisterVO> register(@Valid @RequestBody UserRegisterDTO userRegisterDTO) {
        UserRegisterVO registerVO = userRegisterService.register(userRegisterDTO);
        return R.ok(registerVO, "注册成功");
    }

    /**
     * 检查用户名是否可用
     *
     * @param username 用户名
     * @return 是否可用
     */
    @GetMapping("/check/username")
    @Operation(summary = "检查用户名是否可用", description = "检查用户名是否已被注册")
    public R<Boolean> checkUsername(
            @Parameter(description = "用户名") @RequestParam String username) {
        boolean available = userRegisterService.checkUsernameAvailable(username);
        return R.ok(available);
    }

    /**
     * 检查手机号是否可用
     *
     * @param phone 手机号
     * @return 是否可用
     */
    @GetMapping("/check/phone")
    @Operation(summary = "检查手机号是否可用", description = "检查手机号是否已被注册")
    public R<Boolean> checkPhone(
            @Parameter(description = "手机号") @RequestParam String phone) {
        boolean available = userRegisterService.checkPhoneAvailable(phone);
        return R.ok(available);
    }

    /**
     * 检查邮箱是否可用
     *
     * @param email 邮箱
     * @return 是否可用
     */
    @GetMapping("/check/email")
    @Operation(summary = "检查邮箱是否可用", description = "检查邮箱是否已被注册")
    public R<Boolean> checkEmail(
            @Parameter(description = "邮箱") @RequestParam String email) {
        boolean available = userRegisterService.checkEmailAvailable(email);
        return R.ok(available);
    }
} 