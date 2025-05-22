package com.ptu.template.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 模板状态枚举
 */
@Getter
@AllArgsConstructor
public enum TemplateStatusEnum {

    /**
     * 已下架
     */
    OFFLINE(0, "已下架"),

    /**
     * 已上架
     */
    ONLINE(1, "已上架");

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 状态描述
     */
    private final String desc;

    /**
     * 根据状态码获取枚举
     *
     * @param code 状态码
     * @return 枚举实例
     */
    public static TemplateStatusEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (TemplateStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
} 