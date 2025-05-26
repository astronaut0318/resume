package com.ptu.template.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 模板详情视图对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "模板详情视图对象")
public class TemplateDetailVO extends TemplateVO {

    private static final long serialVersionUID = 1L;

    /**
     * 模板文件URL
     */
    @ApiModelProperty(value = "模板文件URL")
    private String filePath;

    /**
     * 模板描述
     */
    @ApiModelProperty(value = "模板描述")
    private String description;

    /**
     * 状态（0-下架，1-上架）
     */
    @ApiModelProperty(value = "状态（0-下架，1-上架）")
    private Integer status;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 是否已收藏
     */
    @ApiModelProperty(value = "是否已收藏")
    private Boolean collected;
} 