package com.ptu.notification.vo;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 通知列表VO
 */
@Data
public class NotificationVO implements Serializable {
    private Long id;
    private String title;
    private String content;
    private Integer type;
    private Integer isRead;
    private Date createTime;
} 