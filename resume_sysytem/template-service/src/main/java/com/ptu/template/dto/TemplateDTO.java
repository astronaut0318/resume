package com.ptu.template.dto;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 模板数据传输对象
 */
@Data
public class TemplateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 模板ID
     */
    private Long id;
    
    /**
     * 分类ID
     */
    private Long categoryId;
    
    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 模板名称
     */
    private String name;

    /**
     * 缩略图URL
     */
    private String thumbnail;
    
    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 是否免费（0-付费，1-免费）
     */
    private Integer isFree;

    /**
     * 下载次数
     */
    private Integer downloads;
    
    /**
     * 状态：0-下架，1-上架
     */
    private Integer status = 1;
} 