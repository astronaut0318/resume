package com.ptu.auth.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * 修改密码请求DTO
 */
@Data
@ApiModel(value = "修改密码请求DTO", description = "修改密码请求参数")
public class ChangePasswordDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 旧密码
     */
    @NotBlank(message = "旧密码不能为空")
    @ApiModelProperty(value = "旧密码", required = true, example = "password123")
    private String oldPassword;

    /**
     * 新密码
     */
    @NotBlank(message = "新密码不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_]{6,20}$", message = "密码必须为6-20位字母、数字或下划线")
    @ApiModelProperty(value = "新密码", required = true, example = "newPassword456")
    private String newPassword;
} 