package com.ptu.template.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 分享链接视图对象
 */
@Data
@ApiModel(description = "分享链接视图对象")
public class ShareLinkVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分享链接
     */
    @ApiModelProperty(value = "分享链接")
    private String shareUrl;

    /**
     * 过期时间
     */
    @ApiModelProperty(value = "过期时间")
    private LocalDateTime expireTime;
} 