package com.ptu.resume.auth.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码编码器测试工具
 * 用于生成和验证密码哈希
 *
 * @author PTU
 */
public class TestPasswordEncoder {

    public static void main(String[] args) {
        // 创建密码编码器
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // 明文密码
        String rawPassword = "admin123";
        
        // 生成密码哈希
        String encodedPassword = encoder.encode(rawPassword);
        System.out.println("生成的密码哈希: " + encodedPassword);
        
        // 验证密码
        String storedHash = "$2a$10$X9XZ0Uz4jDHR8yH7wGMsIe8AB5P562kc13VxYg3RbE1hUQtzMYSu.";
        boolean matches = encoder.matches(rawPassword, storedHash);
        System.out.println("密码验证结果: " + matches);
        
        // 如果验证失败，尝试使用不同的密码
        String[] possiblePasswords = {"admin", "123456", "password", "admin@123"};
        for (String password : possiblePasswords) {
            boolean match = encoder.matches(password, storedHash);
            System.out.println("尝试密码 [" + password + "] 验证结果: " + match);
        }
    }
} 