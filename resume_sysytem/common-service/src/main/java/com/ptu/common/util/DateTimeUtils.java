package com.ptu.common.util;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 日期时间工具类
 */
@UtilityClass
public class DateTimeUtils {

    /**
     * 日期格式：yyyy-MM-dd
     */
    public static final String DATE_PATTERN = DatePattern.NORM_DATE_PATTERN;

    /**
     * 日期时间格式：yyyy-MM-dd HH:mm:ss
     */
    public static final String DATETIME_PATTERN = DatePattern.NORM_DATETIME_PATTERN;

    /**
     * 时间格式：HH:mm:ss
     */
    public static final String TIME_PATTERN = DatePattern.NORM_TIME_PATTERN;

    /**
     * 获取当前日期
     *
     * @return 当前日期字符串，格式：yyyy-MM-dd
     */
    public static String getCurrentDate() {
        return DateUtil.today();
    }

    /**
     * 获取当前日期时间
     *
     * @return 当前日期时间字符串，格式：yyyy-MM-dd HH:mm:ss
     */
    public static String getCurrentDateTime() {
        return DateUtil.now();
    }

    /**
     * 日期字符串转换为Date对象
     *
     * @param dateStr 日期字符串
     * @param pattern 日期格式
     * @return Date对象
     */
    public static Date parseDate(String dateStr, String pattern) {
        return DateUtil.parse(dateStr, pattern);
    }

    /**
     * Date对象转换为字符串
     *
     * @param date    Date对象
     * @param pattern 日期格式
     * @return 日期字符串
     */
    public static String formatDate(Date date, String pattern) {
        return DateUtil.format(date, pattern);
    }

    /**
     * LocalDate转Date
     *
     * @param localDate LocalDate对象
     * @return Date对象
     */
    public static Date toDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     * LocalDateTime转Date
     *
     * @param localDateTime LocalDateTime对象
     * @return Date对象
     */
    public static Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Date转LocalDate
     *
     * @param date Date对象
     * @return LocalDate对象
     */
    public static LocalDate toLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * Date转LocalDateTime
     *
     * @param date Date对象
     * @return LocalDateTime对象
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * 计算两个日期间隔的天数
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 间隔天数
     */
    public static long betweenDays(Date startDate, Date endDate) {
        return DateUtil.betweenDay(startDate, endDate, true);
    }

    /**
     * 增加天数
     *
     * @param date 日期
     * @param days 天数，负数为减少
     * @return 新的日期
     */
    public static Date addDays(Date date, int days) {
        return DateUtil.offsetDay(date, days);
    }

    /**
     * 增加月份
     *
     * @param date   日期
     * @param months 月数，负数为减少
     * @return 新的日期
     */
    public static Date addMonths(Date date, int months) {
        return DateUtil.offsetMonth(date, months);
    }

    /**
     * 增加年份
     *
     * @param date  日期
     * @param years 年数，负数为减少
     * @return 新的日期
     */
    public static Date addYears(Date date, int years) {
        return DateUtil.offsetMonth(date, years * 12);
    }

    /**
     * 判断是否为闰年
     *
     * @param year 年份
     * @return 是否闰年
     */
    public static boolean isLeapYear(int year) {
        return DateUtil.isLeapYear(year);
    }
} 