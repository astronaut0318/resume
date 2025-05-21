package com.ptu.common.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码工具类
 */
public class PasswordUtil {

    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    /**
     * 加密
     *
     * @param password 明文密码
     * @return 加密后的密码
     */
    public static String encode(String password) {
        return ENCODER.encode(password);
    }

    /**
     * 比较密码是否一致
     *
     * @param rawPassword     明文密码
     * @param encodedPassword 加密后的密码
     * @return 是否一致
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        return ENCODER.matches(rawPassword, encodedPassword);
    }
} 