package com.ptu.user.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ptu.common.api.R;
import com.ptu.user.dto.RegisterDTO;
import com.ptu.user.dto.UserDetailUpdateDTO;
import com.ptu.user.entity.UserEntity;
import com.ptu.user.service.UserDetailService;
import com.ptu.user.service.UserService;
import com.ptu.user.service.UserVipService;
import com.ptu.user.vo.RegisterVO;
import com.ptu.user.vo.UserDetailVO;
import com.ptu.user.vo.UserVipVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * 用户控制器
 */
@Api(tags = "用户管理接口")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserDetailService userDetailService;
    private final UserVipService userVipService;

    /**
     * 用户注册
     */
    @ApiOperation("用户注册")
    @PostMapping("/register")
    public R<RegisterVO> register(@Valid @RequestBody RegisterDTO registerDTO) {
        RegisterVO registerVO = userService.register(registerDTO);
        return R.ok("注册成功", registerVO);
    }

    /**
     * 根据ID获取用户
     */
    @ApiOperation("根据ID获取用户")
    @GetMapping("/{id}")
    public R<UserEntity> getById(
            @ApiParam(value = "用户ID", required = true) @PathVariable Long id) {
        UserEntity user = userService.getById(id);
        return user != null ? R.ok(user) : R.failed("用户不存在");
    }

    /**
     * 根据用户名获取用户
     */
    @ApiOperation("根据用户名获取用户")
    @GetMapping("/by-username/{username}")
    public R<UserEntity> getByUsername(
            @ApiParam(value = "用户名", required = true) @PathVariable String username) {
        UserEntity user = userService.getByUsername(username);
        return user != null ? R.ok(user) : R.failed("用户不存在");
    }

    /**
     * 分页查询用户列表
     */
    @ApiOperation("分页查询用户列表")
    @GetMapping("/page")
    public R<IPage<UserEntity>> pageUsers(
            @ApiParam(value = "页码", defaultValue = "1") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam(value = "每页大小", defaultValue = "10") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("用户名") @RequestParam(required = false) String username,
            @ApiParam("手机号") @RequestParam(required = false) String phone,
            @ApiParam("邮箱") @RequestParam(required = false) String email) {
        IPage<UserEntity> result = userService.pageUsers(page, size, username, phone, email);
        return R.ok(result);
    }

    /**
     * 修改用户状态
     */
    @ApiOperation("修改用户状态")
    @PutMapping("/{id}/status/{status}")
    public R<Boolean> updateStatus(
            @ApiParam(value = "用户ID", required = true) @PathVariable Long id,
            @ApiParam(value = "状态：0-正常，1-禁用", required = true) @PathVariable Integer status) {
        boolean result = userService.updateStatus(id, status);
        return result ? R.ok("修改状态成功", true) : R.failed("修改状态失败");
    }

    /**
     * 重置密码
     */
    @ApiOperation("重置密码")
    @PutMapping("/{id}/reset-password")
    public R<Boolean> resetPassword(
            @ApiParam(value = "用户ID", required = true) @PathVariable Long id,
            @ApiParam(value = "新密码", required = true) @RequestParam String newPassword) {
        boolean result = userService.resetPassword(id, newPassword);
        return result ? R.ok("重置密码成功", true) : R.failed("重置密码失败");
    }
    
    /**
     * 验证用户密码
     */
    @ApiOperation("验证用户密码")
    @PostMapping("/verify-password")
    public R<Boolean> verifyPassword(
            @ApiParam(value = "用户ID", required = true) @RequestParam Long userId,
            @ApiParam(value = "密码", required = true) @RequestParam String password) {
        boolean result = userService.verifyPassword(userId, password);
        return R.ok(result);
    }
    
    /**
     * 更新用户密码
     */
    @ApiOperation("更新用户密码")
    @PostMapping("/update-password")
    public R<Boolean> updatePassword(
            @ApiParam(value = "用户ID", required = true) @RequestParam Long userId,
            @ApiParam(value = "新密码", required = true) @RequestParam String newPassword) {
        boolean result = userService.updatePassword(userId, newPassword);
        return result ? R.ok("更新密码成功", true) : R.failed("更新密码失败");
    }
    
    /**
     * 获取用户详细信息
     */
    @ApiOperation("获取用户详细信息")
    @GetMapping("/{userId}/details")
    public R<UserDetailVO> getUserDetail(
            @ApiParam(value = "用户ID", required = true) @PathVariable Long userId) {
        UserDetailVO userDetailVO = userDetailService.getDetailByUserId(userId);
        return userDetailVO != null ? R.ok(userDetailVO) : R.failed("用户详细信息不存在");
    }
    
    /**
     * 更新用户详细信息
     */
    @ApiOperation("更新用户详细信息")
    @PutMapping("/{userId}/details")
    public R<Boolean> updateUserDetail(
            @ApiParam(value = "用户ID", required = true) @PathVariable Long userId,
            @Valid @RequestBody UserDetailUpdateDTO userDetailUpdateDTO) {
        boolean result = userDetailService.updateDetail(userId, userDetailUpdateDTO);
        return result ? R.ok("更新成功", true) : R.failed("更新失败");
    }
    
    /**
     * 获取用户VIP会员信息
     */
    @ApiOperation("获取用户VIP会员信息")
    @GetMapping("/{userId}/vip")
    public R<UserVipVO> getVipInfo(
            @ApiParam(value = "用户ID", required = true) @PathVariable Long userId) {
        UserVipVO vipVO = userVipService.getVipInfo(userId);
        return R.ok(vipVO);
    }
    
    /**
     * 开通VIP会员
     */
    @ApiOperation("开通VIP会员")
    @PostMapping("/{userId}/vip/open")
    public R<Boolean> openVip(
            @ApiParam(value = "用户ID", required = true) @PathVariable Long userId,
            @ApiParam(value = "VIP等级：1-初级，2-中级，3-高级", required = true) 
            @RequestParam @Min(1) @Max(3) Integer level,
            @ApiParam(value = "开通月数", required = true) 
            @RequestParam @Min(1) Integer months) {
        boolean result = userVipService.openVip(userId, level, months);
        return result ? R.ok("开通成功", true) : R.failed("开通失败");
    }
    
    /**
     * 续费VIP会员
     */
    @ApiOperation("续费VIP会员")
    @PostMapping("/{userId}/vip/renew")
    public R<Boolean> renewVip(
            @ApiParam(value = "用户ID", required = true) @PathVariable Long userId,
            @ApiParam(value = "续费月数", required = true) 
            @RequestParam @Min(1) Integer months) {
        boolean result = userVipService.renewVip(userId, months);
        return result ? R.ok("续费成功", true) : R.failed("续费失败");
    }
    
    /**
     * 升级VIP等级
     */
    @ApiOperation("升级VIP等级")
    @PostMapping("/{userId}/vip/upgrade")
    public R<Boolean> upgradeVip(
            @ApiParam(value = "用户ID", required = true) @PathVariable Long userId,
            @ApiParam(value = "新VIP等级：1-初级，2-中级，3-高级", required = true) 
            @RequestParam @Min(1) @Max(3) Integer level) {
        boolean result = userVipService.upgradeVip(userId, level);
        return result ? R.ok("升级成功", true) : R.failed("升级失败");
    }
} 