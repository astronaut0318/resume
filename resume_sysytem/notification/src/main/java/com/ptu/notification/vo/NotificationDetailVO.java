package com.ptu.notification.vo;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 通知详情VO
 */
@Data
public class NotificationDetailVO implements Serializable {
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private Integer type;
    private Integer isRead;
    private Date createTime;
} 