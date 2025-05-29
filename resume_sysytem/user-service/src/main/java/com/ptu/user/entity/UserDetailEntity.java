package com.ptu.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户详细信息实体类
 */
@Data
@Accessors(chain = true)
@TableName("user_details")
public class UserDetailEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID，关联user表
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
     * 出生日期
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

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 删除标志：0-未删除，1-已删除
     */
    @TableField("deleted")
    private Integer deleted;
} 