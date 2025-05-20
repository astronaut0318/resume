package com.ptu.user.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ptu.common.api.R;
import com.ptu.user.entity.UserEntity;
import com.ptu.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@Api(tags = "用户管理接口")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 根据ID获取用户
     */
    @ApiOperation("根据ID获取用户")
    @GetMapping("/{id}")
    public R<UserEntity> getById(
            @ApiParam(value = "用户ID", required = true) @PathVariable Long id) {
        UserEntity user = userService.getById(id);
        return user != null ? R.ok(user) : R.fail("用户不存在");
    }

    /**
     * 根据用户名获取用户
     */
    @ApiOperation("根据用户名获取用户")
    @GetMapping("/by-username/{username}")
    public R<UserEntity> getByUsername(
            @ApiParam(value = "用户名", required = true) @PathVariable String username) {
        UserEntity user = userService.getByUsername(username);
        return user != null ? R.ok(user) : R.fail("用户不存在");
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
        return result ? R.ok(true, "修改状态成功") : R.fail("修改状态失败");
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
        return result ? R.ok(true, "重置密码成功") : R.fail("重置密码失败");
    }
} 