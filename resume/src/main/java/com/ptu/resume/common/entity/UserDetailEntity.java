package com.ptu.resume.common.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 用户详情实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_details")
public class UserDetailEntity extends BaseEntity {

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 真实姓名
     */
    @TableField("real_name")
    private String realName;

    /**
     * 性别：0-未知，1-男，2-女
     */
    @TableField("gender")
    private Integer gender;

    /**
     * 生日
     */
    @TableField("birthday")
    private LocalDate birthday;

    /**
     * 学历
     */
    @TableField("education")
    private String education;

    /**
     * 工作年限
     */
    @TableField("work_years")
    private Integer workYears;

    /**
     * 地址
     */
    @TableField("address")
    private String address;

    /**
     * 个人简介
     */
    @TableField("profile")
    private String profile;
}