package com.ptu.auth.feign;

import com.ptu.common.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 用户服务Feign客户端
 */
@FeignClient(value = "user-service")
public interface UserFeignClient {

    /**
     * 根据用户名获取用户信息
     *
     * @param username 用户名
     * @return 用户信息
     */
    @GetMapping("/users/by-username/{username}")
    R<?> getUserByUsername(@PathVariable("username") String username);
    
    /**
     * 验证用户密码
     *
     * @param userId 用户ID
     * @param password 密码(明文，会在用户服务中加密后比较)
     * @return 是否匹配
     */
    @PostMapping("/verify-password")
    R<Boolean> verifyPassword(@RequestParam("userId") Long userId, @RequestParam("password") String password);
    
    /**
     * 更新用户密码
     *
     * @param userId 用户ID
     * @param newPassword 新密码(明文，会在用户服务中加密)
     * @return 是否成功
     */
    @PostMapping("/update-password")
    R<Boolean> updatePassword(@RequestParam("userId") Long userId, @RequestParam("newPassword") String newPassword);
} 