package com.ptu.resume.auth.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用户注册响应VO
 * 严格按照API文档规范设计
 *
 * @author PTU
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;
} 