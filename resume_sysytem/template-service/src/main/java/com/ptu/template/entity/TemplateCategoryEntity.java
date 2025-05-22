package com.ptu.template.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 模板分类实体类
 */
@Data
@TableName("template_categories")
@ApiModel(description = "模板分类实体")
public class TemplateCategoryEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "主键ID")
    private Long id;

    /**
     * 分类名称
     */
    @TableField("name")
    @ApiModelProperty(value = "分类名称")
    private String name;

    /**
     * 排序值
     */
    @TableField("sort")
    @ApiModelProperty(value = "排序值")
    private Integer sort;

    /**
     * 状态（0-禁用，1-启用）
     */
    @TableField("status")
    @ApiModelProperty(value = "状态（0-禁用，1-启用）")
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;
} 