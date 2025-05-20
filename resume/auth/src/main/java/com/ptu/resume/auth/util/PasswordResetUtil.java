package com.ptu.resume.auth.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码重置工具类
 * 用于生成密码哈希，以便直接在数据库中更新
 *
 * @author PTU
 */
public class PasswordResetUtil {

    public static void main(String[] args) {
        // 创建密码编码器
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // 明文密码
        String[] passwords = {"admin123", "123456", "admin", "password"};
        
        // 生成密码哈希
        for (String password : passwords) {
            String hash = encoder.encode(password);
            System.out.println(String.format("密码 [%s] 的哈希值: %s", password, hash));
            
            // 用于SQL更新的语句
            System.out.println(String.format("UPDATE auth_user SET password='%s' WHERE username='admin';", hash));
            System.out.println("------------------------------");
        }
    }
} 