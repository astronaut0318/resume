package com.ptu.common.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;
import java.util.UUID;

/**
 * ID生成器工具类
 * 用于生成订单号、交易流水号等唯一标识
 */
@Component
public class IdGenerator {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    /**
     * 生成订单号
     * 格式：年月日时分秒 + 6位随机数
     * 如：20230101120000123456
     * 
     * @return 订单号
     */
    public String generateOrderNo() {
        String dateStr = DateUtil.format(new Date(), "yyyyMMddHHmmss");
        String random = RandomUtil.randomNumbers(6);
        return dateStr + random;
    }

    /**
     * 生成交易流水号
     * 格式：前缀 + 年月日时分秒 + 6位随机数
     * 如：ALI20230101120000123456
     * 
     * @param prefix 前缀，如ALI表示支付宝，WX表示微信
     * @return 交易流水号
     */
    public String generateTradeNo(String prefix) {
        String dateStr = DateUtil.format(new Date(), "yyyyMMddHHmmss");
        String random = RandomUtil.randomNumbers(6);
        return prefix + dateStr + random;
    }

    /**
     * 生成通用唯一标识符（UUID），去除"-"
     *
     * @return UUID字符串
     */
    public String generateUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 生成文件名
     * 格式：前缀_年月日时分秒_随机数.扩展名
     *
     * @param prefix    文件名前缀
     * @param extension 文件扩展名（不含点号）
     * @return 文件名
     */
    public String generateFileName(String prefix, String extension) {
        String dateTime = LocalDateTime.now().format(DATE_TIME_FORMATTER);
        String randomNum = String.format("%04d", ThreadLocalRandom.current().nextInt(1000, 9999));
        return prefix + "_" + dateTime + "_" + randomNum + "." + extension;
    }
} 