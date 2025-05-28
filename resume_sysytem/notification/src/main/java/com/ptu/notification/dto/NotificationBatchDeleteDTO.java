package com.ptu.notification.dto;

import lombok.Data;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * 批量删除通知请求DTO
 */
@Data
public class NotificationBatchDeleteDTO implements Serializable {
    /** 通知ID列表，必填 */
    @NotEmpty(message = "通知ID列表不能为空")
    private List<Long> notificationIds;
} 