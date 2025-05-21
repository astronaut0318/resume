package com.ptu.auth.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 刷新令牌请求DTO
 */
@Data
@ApiModel(value = "刷新令牌请求DTO", description = "刷新令牌请求参数")
public class RefreshTokenDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 刷新令牌
     */
    @NotBlank(message = "刷新令牌不能为空")
    @ApiModelProperty(value = "刷新令牌", required = true, example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String refreshToken;
} 