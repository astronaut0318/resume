package com.ptu.user.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户信息数据传输对象
 */
@Data
public class UserInfoDTO {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 角色：0-普通用户，1-VIP用户，2-管理员
     */
    private Integer role;

    /**
     * 账号状态：0-禁用，1-正常
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
} 