package com.ptu.template.common.query;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 模板查询条件
 */
@Data
@ApiModel(description = "模板查询条件")
public class TemplateQuery {

    /**
     * 分类ID
     */
    @ApiModelProperty(value = "分类ID")
    private Long categoryId;

    /**
     * 是否免费（0-付费，1-免费）
     */
    @ApiModelProperty(value = "是否免费（0-付费，1-免费）")
    private Integer isFree;

    /**
     * 搜索关键词
     */
    @ApiModelProperty(value = "搜索关键词")
    private String keyword;

    /**
     * 页码（默认1）
     */
    @ApiModelProperty(value = "页码（默认1）")
    private Integer page = 1;

    /**
     * 每页大小（默认10）
     */
    @ApiModelProperty(value = "每页大小（默认10）")
    private Integer size = 10;
} 