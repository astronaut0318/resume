package com.ptu.template.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 模板视图对象
 */
@Data
@ApiModel(description = "模板视图对象")
public class TemplateVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 模板ID
     */
    @ApiModelProperty(value = "模板ID")
    private Long id;
    
    /**
     * 分类ID
     */
    @ApiModelProperty(value = "分类ID")
    private Long categoryId;
    
    /**
     * 分类名称
     */
    @ApiModelProperty(value = "分类名称")
    private String categoryName;

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
     * 下载次数
     */
    @ApiModelProperty(value = "下载次数")
    private Integer downloads;
    
    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;
} 