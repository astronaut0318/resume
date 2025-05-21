package com.ptu.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户VIP会员信息数据传输对象
 */
@Data
public class UserVipDTO {

    /**
     * 是否为VIP会员
     */
    private Boolean isVip;

    /**
     * VIP等级：1-初级，2-中级，3-高级
     */
    private Integer level;

    /**
     * 会员开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    /**
     * 会员结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    /**
     * 剩余天数
     */
    private Integer remainingDays;
} 