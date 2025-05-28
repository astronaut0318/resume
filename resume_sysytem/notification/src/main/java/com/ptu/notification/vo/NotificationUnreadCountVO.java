package com.ptu.notification.vo;

import lombok.Data;
import java.io.Serializable;

/**
 * 未读通知数量VO
 */
@Data
public class NotificationUnreadCountVO implements Serializable {
    private Integer count;
} 