package com.ptu.notification.service;

import com.ptu.notification.dto.*;
import com.ptu.notification.vo.*;
import java.util.List;

/**
 * 通知服务接口
 */
public interface NotificationService {
    /** 创建通知 */
    void addNotification(NotificationCreateDTO dto);
    /** 获取通知列表 */
    List<NotificationVO> listNotifications(Long userId, Integer isRead, Integer type, int page, int size);
    /** 获取通知详情 */
    NotificationDetailVO getNotificationDetail(Long notificationId);
    /** 标记通知为已读 */
    void markAsRead(Long notificationId);
    /** 批量标记为已读 */
    void batchMarkAsRead(NotificationBatchReadDTO dto);
    /** 批量删除通知 */
    void batchDelete(NotificationBatchDeleteDTO dto);
    /** 删除单条通知 */
    void deleteNotification(Long notificationId);
    /** 获取未读通知数量 */
    NotificationUnreadCountVO getUnreadCount(Long userId);
    /** 发送邮件通知（Mock） */
    void sendEmailNotification(NotificationEmailDTO dto);
} 