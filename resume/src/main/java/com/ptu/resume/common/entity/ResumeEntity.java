package com.ptu.resume.common.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 简历实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("resumes")
public class ResumeEntity extends BaseEntity {

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 简历标题
     */
    @TableField("title")
    private String title;

    /**
     * 模板ID
     */
    @TableField("template_id")
    private Long templateId;

    /**
     * 简历内容（JSON格式）
     */
    @TableField("content")
    private String content;

    /**
     * 状态：0-草稿，1-已完成
     */
    @TableField("status")
    private Integer status;

    /**
     * 是否默认简历：0-否，1-是
     */
    @TableField("is_default")
    private Integer isDefault;
}