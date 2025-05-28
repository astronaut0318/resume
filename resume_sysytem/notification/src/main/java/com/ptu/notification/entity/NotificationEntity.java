package com.ptu.notification.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

/**
 * 通知实体类，对应数据库notifications表
 */
@Data
@TableName("notifications")
public class NotificationEntity {
    /** 通知主键ID */
    @TableId
    private Long id;
    /** 用户ID */
    private Long userId;
    /** 标题 */
    private String title;
    /** 内容 */
    private String content;
    /** 类型：1-系统通知，2-订单通知，3-其他 */
    private Integer type;
    /** 是否已读：0-未读，1-已读 */
    private Integer isRead;
    /** 创建时间 */
    private Date createTime;
} 