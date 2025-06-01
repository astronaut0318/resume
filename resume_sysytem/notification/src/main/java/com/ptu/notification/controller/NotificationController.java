package com.ptu.notification.controller;

import com.ptu.notification.service.NotificationService;
import com.ptu.notification.vo.NotificationUnreadCountVO;
import com.ptu.common.api.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @Autowired
    private HttpServletRequest request;

    /**
     * 获取未读通知数量
     * @return 未读数量
     */
    @GetMapping("/unread-count")
    public R<NotificationUnreadCountVO> getUnreadCount() {
        String userIdStr = request.getHeader("X-User-Id");
        if (userIdStr == null) {
            return R.failed("未获取到用户ID");
        }
        Long userId = Long.valueOf(userIdStr);
        NotificationUnreadCountVO vo = notificationService.getUnreadCount(userId);
        return R.ok(vo);
    }
} 