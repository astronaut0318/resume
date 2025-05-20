package com.ptu.resume.core.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 日期工具类
 *
 * @author PTU开发团队
 */
public class DateUtils {

    /**
     * 标准日期格式
     */
    public static final String PATTERN_DATE = "yyyy-MM-dd";
    
    /**
     * 标准日期时间格式
     */
    public static final String PATTERN_DATETIME = "yyyy-MM-dd HH:mm:ss";
    
    /**
     * 紧凑型日期格式
     */
    public static final String PATTERN_DATE_COMPACT = "yyyyMMdd";
    
    /**
     * 紧凑型日期时间格式
     */
    public static final String PATTERN_DATETIME_COMPACT = "yyyyMMddHHmmss";

    /**
     * 获取当前日期
     *
     * @return 当前日期字符串，格式：yyyy-MM-dd
     */
    public static String getCurrentDate() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern(PATTERN_DATE));
    }

    /**
     * 获取当前日期时间
     *
     * @return 当前日期时间字符串，格式：yyyy-MM-dd HH:mm:ss
     */
    public static String getCurrentDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(PATTERN_DATETIME));
    }

    /**
     * 日期格式化
     *
     * @param date 日期
     * @param pattern 格式
     * @return 格式化后的日期字符串
     */
    public static String format(Date date, String pattern) {
        if (date == null) {
            return null;
        }
        LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        return localDateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 字符串解析为日期
     *
     * @param dateStr 日期字符串
     * @param pattern 格式
     * @return 日期对象
     */
    public static Date parse(String dateStr, String pattern) {
        try {
            return new SimpleDateFormat(pattern).parse(dateStr);
        } catch (ParseException e) {
            throw new RuntimeException("解析日期字符串失败", e);
        }
    }

    /**
     * 计算两个日期之间的天数差
     *
     * @param date1 日期1
     * @param date2 日期2
     * @return 天数差
     */
    public static long daysBetween(Date date1, Date date2) {
        LocalDate localDate1 = date1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate localDate2 = date2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return Math.abs(localDate1.toEpochDay() - localDate2.toEpochDay());
    }

    /**
     * 增加天数
     *
     * @param date 日期
     * @param days 天数
     * @return 增加天数后的日期
     */
    public static Date addDays(Date date, int days) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        LocalDateTime resultDateTime = localDateTime.plusDays(days);
        return Date.from(resultDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 增加月数
     *
     * @param date 日期
     * @param months 月数
     * @return 增加月数后的日期
     */
    public static Date addMonths(Date date, int months) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        LocalDateTime resultDateTime = localDateTime.plusMonths(months);
        return Date.from(resultDateTime.atZone(ZoneId.systemDefault()).toInstant());
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
     * Date转LocalDateTime
     *
     * @param date Date对象
     * @return LocalDateTime对象
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }
} 