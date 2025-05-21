package com.ptu.auth.controller;

import com.ptu.auth.dto.ChangePasswordDTO;
import com.ptu.auth.dto.LoginDTO;
import com.ptu.auth.dto.RefreshTokenDTO;
import com.ptu.auth.service.AuthService;
import com.ptu.auth.util.JwtTokenUtil;
import com.ptu.auth.vo.TokenVO;
import com.ptu.common.api.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 认证控制器
 */
@Api(tags = "认证管理接口")
@RestController
@RequestMapping
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtTokenUtil jwtTokenUtil;

    /**
     * 登录
     *
     * @param loginDTO 登录参数
     * @return 令牌信息
     */
    @ApiOperation("登录")
    @PostMapping("/login")
    public R<TokenVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        TokenVO tokenVO = authService.login(loginDTO);
        return R.ok("登录成功", tokenVO);
    }

    /**
     * 刷新令牌
     *
     * @param refreshTokenDTO 刷新令牌参数
     * @return 令牌信息
     */
    @ApiOperation("刷新令牌")
    @PostMapping("/refresh")
    public R<TokenVO> refresh(@Valid @RequestBody RefreshTokenDTO refreshTokenDTO) {
        TokenVO tokenVO = authService.refresh(refreshTokenDTO);
        return R.ok("刷新成功", tokenVO);
    }

    /**
     * 登出
     *
     * @param request HTTP请求
     * @return 结果
     */
    @ApiOperation("登出")
    @PostMapping("/logout")
    public R<Boolean> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null) {
            String token = jwtTokenUtil.extractTokenFromHeader(authHeader);
            if (token != null) {
                Long userId = jwtTokenUtil.getUserIdFromToken(token);
                boolean result = authService.logout(userId);
                return R.ok("退出成功", result);
            }
        }
        return R.ok("退出成功", true);
    }

    /**
     * 修改密码
     *
     * @param changePasswordDTO 修改密码参数
     * @param request HTTP请求
     * @return 结果
     */
    @ApiOperation("修改密码")
    @PutMapping("/password")
    public R<Boolean> changePassword(@Valid @RequestBody ChangePasswordDTO changePasswordDTO, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null) {
            String token = jwtTokenUtil.extractTokenFromHeader(authHeader);
            if (token != null) {
                Long userId = jwtTokenUtil.getUserIdFromToken(token);
                boolean result = authService.changePassword(userId, changePasswordDTO);
                return result ? R.ok("密码修改成功", true) : R.failed("密码修改失败");
            }
        }
        return R.failed("未授权");
    }
} 