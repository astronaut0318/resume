package com.ptu.order.template.vo;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 模板视图对象
 */
@Data
public class TemplateVO implements Serializable {

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
     * 模板文件路径
     */
    private String filePath;
    
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
     * 描述
     */
    private String description;
    
    /**
     * 状态：0-下架，1-上架
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 