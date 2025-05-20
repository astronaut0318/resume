package com.ptu.resume.core.utils;

import java.util.Collection;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 *
 * @author PTU开发团队
 */
public class StringUtils {
    
    /**
     * 判断字符串是否为空
     *
     * @param str 字符串
     * @return true:为空 false:不为空
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }
    
    /**
     * 判断字符串是否不为空
     *
     * @param str 字符串
     * @return true:不为空 false:为空
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
    
    /**
     * 判断字符串是否为空白
     *
     * @param str 字符串
     * @return true:为空白 false:不为空白
     */
    public static boolean isBlank(String str) {
        if (str == null) {
            return true;
        }
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 判断字符串是否不为空白
     *
     * @param str 字符串
     * @return true:不为空白 false:为空白
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }
    
    /**
     * 生成UUID（不带横线）
     *
     * @return UUID字符串
     */
    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    
    /**
     * 将驼峰式命名的字符串转换为下划线格式
     *
     * @param str 驼峰式命名的字符串
     * @return 下划线格式的字符串
     */
    public static String camelToUnderscore(String str) {
        if (isEmpty(str)) {
            return str;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i > 0) {
                    sb.append('_');
                }
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
    
    /**
     * 将下划线命名的字符串转换为驼峰式
     *
     * @param str 下划线命名的字符串
     * @return 驼峰式命名的字符串
     */
    public static String underscoreToCamel(String str) {
        if (isEmpty(str)) {
            return str;
        }
        StringBuilder sb = new StringBuilder();
        boolean nextUpper = false;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '_') {
                nextUpper = true;
            } else {
                if (nextUpper) {
                    sb.append(Character.toUpperCase(c));
                    nextUpper = false;
                } else {
                    sb.append(c);
                }
            }
        }
        return sb.toString();
    }
    
    /**
     * 判断集合是否为空
     *
     * @param collection 集合
     * @return true:为空 false:不为空
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }
    
    /**
     * 判断集合是否不为空
     *
     * @param collection 集合
     * @return true:不为空 false:为空
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }
    
    /**
     * 检查字符串是否是合法的手机号
     *
     * @param phone 手机号
     * @return true:是 false:否
     */
    public static boolean isValidPhone(String phone) {
        if (isEmpty(phone)) {
            return false;
        }
        return Pattern.matches("^1[3-9]\\d{9}$", phone);
    }
    
    /**
     * 检查字符串是否是合法的邮箱地址
     *
     * @param email 邮箱地址
     * @return true:是 false:否
     */
    public static boolean isValidEmail(String email) {
        if (isEmpty(email)) {
            return false;
        }
        return Pattern.matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$", email);
    }
    
    /**
     * 字符串拼接
     *
     * @param strs 字符串数组
     * @return 拼接后的字符串
     */
    public static String concat(String... strs) {
        StringBuilder sb = new StringBuilder();
        for (String str : strs) {
            if (str != null) {
                sb.append(str);
            }
        }
        return sb.toString();
    }
} 