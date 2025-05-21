package com.ptu.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * 用户注册DTO
 */
@Data
@ApiModel(value = "用户注册DTO", description = "用户注册请求参数")
public class RegisterDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_]{4,16}$", message = "用户名必须为4-16位字母、数字或下划线")
    @ApiModelProperty(value = "用户名", required = true, example = "zhangsan")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_]{6,20}$", message = "密码必须为6-20位字母、数字或下划线")
    @ApiModelProperty(value = "密码", required = true, example = "password123")
    private String password;

    /**
     * 邮箱
     */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @ApiModelProperty(value = "邮箱", required = true, example = "zhangsan@example.com")
    private String email;

    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @ApiModelProperty(value = "手机号", required = true, example = "13800138000")
    private String phone;
} 