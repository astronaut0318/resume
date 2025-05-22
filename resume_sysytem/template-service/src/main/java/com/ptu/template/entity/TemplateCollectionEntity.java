package com.ptu.template.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 模板收藏实体类
 */
@Data
@TableName("template_collection")
@ApiModel(description = "模板收藏实体")
public class TemplateCollectionEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "主键ID")
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    @ApiModelProperty(value = "用户ID")
    private Long userId;

    /**
     * 模板ID
     */
    @TableField("template_id")
    @ApiModelProperty(value = "模板ID")
    private Long templateId;

    /**
     * 收藏时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @ApiModelProperty(value = "收藏时间")
    private LocalDateTime createTime;
}