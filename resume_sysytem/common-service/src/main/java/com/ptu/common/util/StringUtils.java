package com.ptu.common.util;

import cn.hutool.core.util.StrUtil;
import lombok.experimental.UtilityClass;

import java.util.UUID;

/**
 * 字符串工具类
 */
@UtilityClass
public class StringUtils extends StrUtil {

    /**
     * 生成UUID（不含连字符）
     *
     * @return UUID字符串
     */
    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 生成包含连字符的UUID
     *
     * @return 原始UUID字符串
     */
    public static String uuidWithHyphen() {
        return UUID.randomUUID().toString();
    }

    /**
     * 截取字符串
     *
     * @param str   源字符串
     * @param start 开始位置（包含）
     * @param end   结束位置（不包含）
     * @return 截取后的字符串
     */
    public static String substring(String str, int start, int end) {
        if (isEmpty(str)) {
            return EMPTY;
        }
        if (start < 0) {
            start = 0;
        }
        if (end > str.length()) {
            end = str.length();
        }
        if (start > end) {
            return EMPTY;
        }
        return str.substring(start, end);
    }

    /**
     * 截取字符串，从指定位置开始截取指定长度
     *
     * @param str    源字符串
     * @param start  开始位置（包含）
     * @param length 截取长度
     * @return 截取后的字符串
     */
    public static String substring(String str, int start, int length, boolean ellipsis) {
        if (isEmpty(str)) {
            return EMPTY;
        }
        if (start < 0) {
            start = 0;
        }
        if (length < 0) {
            length = 0;
        }
        int end = start + length;
        if (end > str.length()) {
            end = str.length();
        }
        String result = str.substring(start, end);
        if (ellipsis && end < str.length()) {
            result = result + "...";
        }
        return result;
    }

    /**
     * 判断字符串是否为空或长度为0
     *
     * @param str 字符串
     * @return 是否为空
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    /**
     * 判断字符串是否不为空且长度不为0
     *
     * @param str 字符串
     * @return 是否不为空
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * 判断字符串是否为空或仅包含空白字符
     *
     * @param str 字符串
     * @return 是否为空或仅包含空白字符
     */
    public static boolean isBlank(String str) {
        if (isEmpty(str)) {
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
     * 判断字符串是否不为空且不仅包含空白字符
     *
     * @param str 字符串
     * @return 是否不为空且不仅包含空白字符
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    /**
     * 检查字符串是否以指定前缀开始，忽略大小写
     *
     * @param str    字符串
     * @param prefix 前缀
     * @return 是否以指定前缀开始
     */
    public static boolean startsWithIgnoreCase(String str, String prefix) {
        if (str == null || prefix == null) {
            return str == prefix;
        }
        if (prefix.length() > str.length()) {
            return false;
        }
        return str.regionMatches(true, 0, prefix, 0, prefix.length());
    }

    /**
     * 检查字符串是否以指定后缀结束，忽略大小写
     *
     * @param str    字符串
     * @param suffix 后缀
     * @return 是否以指定后缀结束
     */
    public static boolean endsWithIgnoreCase(String str, String suffix) {
        if (str == null || suffix == null) {
            return str == suffix;
        }
        if (suffix.length() > str.length()) {
            return false;
        }
        return str.regionMatches(true, str.length() - suffix.length(), suffix, 0, suffix.length());
    }
} 