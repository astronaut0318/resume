package com.ptu.resume.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试控制器
 *
 * @author PTU
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * 测试密码哈希
     *
     * @param password 明文密码
     * @return 密码哈希
     */
    @GetMapping("/encode")
    public String encode(@RequestParam String password) {
        return passwordEncoder.encode(password);
    }

    /**
     * 测试密码验证
     *
     * @param password 明文密码
     * @param hash 密码哈希
     * @return 验证结果
     */
    @GetMapping("/verify")
    public boolean verify(@RequestParam String password, @RequestParam String hash) {
        return passwordEncoder.matches(password, hash);
    }

    /**
     * 测试验证admin密码
     *
     * @param password 明文密码
     * @return 验证结果
     */
    @GetMapping("/admin")
    public boolean verifyAdmin(@RequestParam String password) {
        // 数据库中存储的admin用户密码哈希
        String adminHash = "$2a$10$X9XZ0Uz4jDHR8yH7wGMsIe8AB5P562kc13VxYg3RbE1hUQtzMYSu.";
        return passwordEncoder.matches(password, adminHash);
    }
} 