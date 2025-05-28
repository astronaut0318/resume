package com.ptu.notification.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * 邮件通知请求DTO
 */
@Data
public class NotificationEmailDTO implements Serializable {
    /** 接收用户ID列表，为空则发送给所有用户 */
    private List<Long> userIds;
    /** 邮件主题，必填 */
    @NotBlank(message = "主题不能为空")
    private String subject;
    /** 邮件内容，必填 */
    @NotBlank(message = "内容不能为空")
    private String content;
    /** 是否同时保存到系统通知 */
    private Boolean saveToSystem;
} 