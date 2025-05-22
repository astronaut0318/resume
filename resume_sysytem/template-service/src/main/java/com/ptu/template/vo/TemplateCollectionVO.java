package com.ptu.template.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 模板收藏视图对象
 */
@Data
@ApiModel(description = "模板收藏视图对象")
public class TemplateCollectionVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 收藏ID
     */
    @ApiModelProperty(value = "收藏ID")
    private Long id;

    /**
     * 模板ID
     */
    @ApiModelProperty(value = "模板ID")
    private Long templateId;

    /**
     * 模板名称
     */
    @ApiModelProperty(value = "模板名称")
    private String name;

    /**
     * 缩略图URL
     */
    @ApiModelProperty(value = "缩略图URL")
    private String thumbnail;

    /**
     * 价格
     */
    @ApiModelProperty(value = "价格")
    private BigDecimal price;

    /**
     * 是否免费（0-付费，1-免费）
     */
    @ApiModelProperty(value = "是否免费（0-付费，1-免费）")
    private Integer isFree;

    /**
     * 收藏时间
     */
    @ApiModelProperty(value = "收藏时间")
    private LocalDateTime collectTime;
} 