package com.ptu.resume.auth.controller;

import com.ptu.resume.auth.dto.LoginDTO;
import com.ptu.resume.auth.dto.RegisterDTO;
import com.ptu.resume.auth.service.UserService;
import com.ptu.resume.auth.vo.TokenVO;
import com.ptu.resume.auth.vo.UserVO;
import com.ptu.resume.core.model.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 认证控制器
 *
 * @author PTU
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "认证服务", description = "认证服务相关接口")
public class AuthController {

    private final UserService userService;

    /**
     * 健康检查接口
     *
     * @return R
     */
    @GetMapping("/health")
    public R<String> health() {
        return R.ok("Auth service is running");
    }

    /**
     * 用户登录
     *
     * @param loginDTO 登录信息
     * @return 令牌信息
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户登录并返回令牌信息")
    public R<TokenVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        TokenVO tokenVO = userService.login(loginDTO);
        return R.ok(tokenVO);
    }

    /**
     * 用户注册
     *
     * @param registerDTO 注册信息
     * @return 注册结果
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "用户注册并返回注册结果")
    public R<Long> register(@Valid @RequestBody RegisterDTO registerDTO) {
        Long userId = userService.register(registerDTO);
        return R.ok(userId);
    }

    /**
     * 刷新令牌
     *
     * @param refreshToken 刷新令牌
     * @return 新的令牌信息
     */
    @PostMapping("/refresh")
    @Operation(summary = "刷新令牌", description = "通过刷新令牌获取新的访问令牌")
    public R<TokenVO> refreshToken(@RequestParam String refreshToken) {
        TokenVO tokenVO = userService.refreshToken(refreshToken);
        return R.ok(tokenVO);
    }

    /**
     * 获取当前用户信息
     *
     * @param request 请求对象
     * @return 用户信息
     */
    @GetMapping("/user/info")
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息")
    public R<UserVO> getUserInfo(HttpServletRequest request) {
        // 从请求头获取令牌并解析用户ID
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        // 这里简化处理，假设token就是用户ID
        Long userId = Long.valueOf(token);
        UserVO userVO = userService.getUserById(userId);
        return R.ok(userVO);
    }

    /**
     * 退出登录
     *
     * @param request 请求对象
     * @return 操作结果
     */
    @PostMapping("/logout")
    @Operation(summary = "退出登录", description = "用户退出登录")
    public R<Boolean> logout(HttpServletRequest request) {
        // 实际项目中应该从请求头获取令牌并清除对应的令牌缓存
        return R.ok(true);
    }
}