package com.ptu.notification.controller;

import com.ptu.notification.service.NotificationService;
import com.ptu.notification.vo.NotificationUnreadCountVO;
import com.ptu.notification.vo.NotificationVO;
import com.ptu.notification.vo.NotificationDetailVO;
import com.ptu.notification.dto.NotificationCreateDTO;
import com.ptu.notification.dto.NotificationBatchReadDTO;
import com.ptu.notification.dto.NotificationBatchDeleteDTO;
import com.ptu.notification.dto.NotificationEmailDTO;
import com.ptu.common.api.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @Autowired
    private HttpServletRequest request;

    // 添加setter方法用于测试
    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
    
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    /**
     * 获取当前用户ID
     * @return 用户ID
     */
    private Long getCurrentUserId() {
        // 尝试从X-User-Id获取
        String userIdStr = request.getHeader("X-User-Id");
        
        // 如果X-User-Id为空，尝试从userId获取
        if (userIdStr == null) {
            userIdStr = request.getHeader("userId");
        }
        
        // 如果都为空，使用默认值1（测试环境）
        if (userIdStr == null) {
            return 1L; // 开发环境默认用户
        }
        
        return Long.valueOf(userIdStr);
    }

    /**
     * 创建新通知
     * @param dto 通知数据
     * @return 结果
     */
    @PostMapping
    public R<Void> createNotification(@RequestBody NotificationCreateDTO dto) {
        try {
            // 如果未提供userId，则使用当前登录用户ID
            if (dto.getUserId() == null) {
                dto.setUserId(getCurrentUserId());
            }
            
            // 手动验证必填字段
            if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
                return R.failed("标题不能为空");
            }
            if (dto.getContent() == null || dto.getContent().trim().isEmpty()) {
                return R.failed("内容不能为空");
            }
            if (dto.getType() == null) {
                return R.failed("类型不能为空");
            }
            
            notificationService.addNotification(dto);
            return R.ok();
        } catch (Exception e) {
            return R.failed(e.getMessage());
        }
    }

    /**
     * 获取未读通知数量
     * @return 未读数量
     */
    @GetMapping("/unread-count")
    public R<NotificationUnreadCountVO> getUnreadCount() {
        Long userId = getCurrentUserId();
        NotificationUnreadCountVO vo = notificationService.getUnreadCount(userId);
        return R.ok(vo);
    }
    
    /**
     * 获取通知列表
     * @param isRead 是否已读(可选，0-未读，1-已读)
     * @param type 通知类型(可选，1-系统通知，2-订单通知，3-其他)
     * @param page 页码(默认1)
     * @param size 每页大小(默认10)
     * @return 通知列表
     */
    @GetMapping
    public R<Map<String, Object>> getNotifications(
            @RequestParam(required = false) Integer isRead,
            @RequestParam(required = false) Integer type,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Long userId = getCurrentUserId();
        List<NotificationVO> list = notificationService.listNotifications(userId, isRead, type, page, size);
        
        // 简单模拟分页数据
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", list.size() + (page > 1 ? (page - 1) * size : 0)); // 简单模拟总数
        
        return R.ok(result);
    }
    
    /**
     * 获取通知详情
     * @param notificationId 通知ID
     * @return 通知详情
     */
    @GetMapping("/{notificationId}")
    public R<NotificationDetailVO> getNotificationDetail(@PathVariable Long notificationId) {
        NotificationDetailVO vo = notificationService.getNotificationDetail(notificationId);
        if (vo == null) {
            return R.failed("通知不存在");
        }
        return R.ok(vo);
    }
    
    /**
     * 标记通知为已读
     * @param notificationId 通知ID
     * @return 结果
     */
    @PutMapping("/{notificationId}/read")
    public R<Void> markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return R.ok();
    }
    
    /**
     * 标记所有通知为已读
     * @return 结果
     */
    @PutMapping("/read-all")
    public R<Void> markAllAsRead() {
        try {
            Long userId = getCurrentUserId();
            
            // 查询该用户所有未读通知
            List<NotificationVO> unreadList = notificationService.listNotifications(
                userId, 0, null, 1, Integer.MAX_VALUE);
            
            if (unreadList != null && !unreadList.isEmpty()) {
                NotificationBatchReadDTO dto = new NotificationBatchReadDTO();
                List<Long> notificationIds = unreadList.stream()
                    .map(NotificationVO::getId)
                    .collect(java.util.stream.Collectors.toList());
                
                dto.setNotificationIds(notificationIds);
                notificationService.batchMarkAsRead(dto);
            }
            
            return R.ok();
        } catch (Exception e) {
            return R.failed(e.getMessage());
        }
    }
    
    /**
     * 删除通知
     * @param notificationId 通知ID
     * @return 结果
     */
    @DeleteMapping("/{notificationId}")
    public R<Void> deleteNotification(@PathVariable Long notificationId) {
        notificationService.deleteNotification(notificationId);
        return R.ok();
    }
    
    /**
     * 发送邮件通知(仅管理员)
     * @param dto 发送数据
     * @return 结果
     */
    @PostMapping("/email")
    public R<Map<String, Integer>> sendEmailNotification(@RequestBody @Valid NotificationEmailDTO dto) {
        notificationService.sendEmailNotification(dto);
        
        // 模拟发送结果
        Map<String, Integer> result = new HashMap<>();
        result.put("successCount", dto.getUserIds() != null ? dto.getUserIds().size() : 10);
        result.put("failCount", 0);
        
        return R.ok(result);
    }
} 