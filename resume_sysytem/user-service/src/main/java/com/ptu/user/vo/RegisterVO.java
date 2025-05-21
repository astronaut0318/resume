package com.ptu.user.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 注册成功返回VO
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "注册成功返回VO", description = "注册成功后返回的用户信息")
public class RegisterVO implements Serializable {

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
} 