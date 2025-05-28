package com.ptu.notification.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 通知创建请求DTO
 */
@Data
public class NotificationCreateDTO implements Serializable {
    /** 用户ID，必填 */
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    /** 标题，必填 */
    @NotBlank(message = "标题不能为空")
    private String title;
    /** 内容，必填 */
    @NotBlank(message = "内容不能为空")
    private String content;
    /** 类型，必填 */
    @NotNull(message = "类型不能为空")
    private Integer type;
} 