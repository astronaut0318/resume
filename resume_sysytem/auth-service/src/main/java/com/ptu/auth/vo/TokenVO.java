package com.ptu.auth.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 令牌响应VO
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "令牌响应VO", description = "令牌响应信息")
public class TokenVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID", example = "1")
    private Long userId;

    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户名", example = "zhangsan")
    private String username;

    /**
     * 访问令牌
     */
    @ApiModelProperty(value = "访问令牌", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    /**
     * 刷新令牌
     */
    @ApiModelProperty(value = "刷新令牌", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String refreshToken;

    /**
     * 过期时间(秒)
     */
    @ApiModelProperty(value = "过期时间(秒)", example = "3600")
    private Long expiresIn;
} 