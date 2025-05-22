package com.ptu.template.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 模板实体类
 */
@Data
@TableName("templates")
@ApiModel(description = "模板实体")
public class TemplateEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "主键ID")
    private Long id;

    /**
     * 分类ID
     */
    @TableField("category_id")
    @ApiModelProperty(value = "分类ID")
    private Long categoryId;

    /**
     * 模板名称
     */
    @TableField("name")
    @ApiModelProperty(value = "模板名称")
    private String name;

    /**
     * 缩略图URL
     */
    @TableField("thumbnail")
    @ApiModelProperty(value = "缩略图URL")
    private String thumbnail;

    /**
     * 模板文件URL
     */
    @TableField("file_path")
    @ApiModelProperty(value = "模板文件URL")
    private String filePath;

    /**
     * 价格
     */
    @TableField("price")
    @ApiModelProperty(value = "价格")
    private BigDecimal price;

    /**
     * 是否免费（0-付费，1-免费）
     */
    @TableField("is_free")
    @ApiModelProperty(value = "是否免费（0-付费，1-免费）")
    private Integer isFree;

    /**
     * 下载次数
     */
    @TableField("downloads")
    @ApiModelProperty(value = "下载次数")
    private Integer downloads;

    /**
     * 模板描述
     */
    @TableField("description")
    @ApiModelProperty(value = "模板描述")
    private String description;

    /**
     * 状态（0-下架，1-上架）
     */
    @TableField("status")
    @ApiModelProperty(value = "状态（0-下架，1-上架）")
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