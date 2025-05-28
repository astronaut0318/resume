package com.ptu.notification.service.impl;

import com.ptu.notification.dto.*;
import com.ptu.notification.vo.*;
import com.ptu.notification.entity.NotificationEntity;
import com.ptu.notification.mapper.NotificationMapper;
import com.ptu.notification.feign.UserFeignClient;
import com.ptu.notification.service.NotificationService;
import com.ptu.common.api.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 通知服务实现类
 */
@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;
    @Autowired
    private UserFeignClient userFeignClient;

    @Override
    public void addNotification(NotificationCreateDTO dto) {
        // 校验用户存在
        R<Object> userResp = userFeignClient.getUserById(dto.getUserId());
        if (userResp == null || userResp.getCode() != 200) {
            throw new RuntimeException("用户不存在");
        }
        NotificationEntity entity = new NotificationEntity();
        BeanUtils.copyProperties(dto, entity);
        entity.setIsRead(0);
        entity.setCreateTime(new Date());
        notificationMapper.insert(entity);
    }

    @Override
    public List<NotificationVO> listNotifications(Long userId, Integer isRead, Integer type, int page, int size) {
        // 这里只做简单分页与条件过滤，实际可用MyBatis Plus分页
        List<NotificationEntity> all = notificationMapper.selectList(null);
        return all.stream()
                .filter(n -> Objects.equals(n.getUserId(), userId))
                .filter(n -> isRead == null || Objects.equals(n.getIsRead(), isRead))
                .filter(n -> type == null || Objects.equals(n.getType(), type))
                .skip((long) (page - 1) * size)
                .limit(size)
                .map(n -> {
                    NotificationVO vo = new NotificationVO();
                    BeanUtils.copyProperties(n, vo);
                    return vo;
                })
                .collect(Collectors.toList());
    }

    @Override
    public NotificationDetailVO getNotificationDetail(Long notificationId) {
        NotificationEntity n = notificationMapper.selectById(notificationId);
        if (n == null) return null;
        NotificationDetailVO vo = new NotificationDetailVO();
        BeanUtils.copyProperties(n, vo);
        return vo;
    }

    @Override
    public void markAsRead(Long notificationId) {
        NotificationEntity n = notificationMapper.selectById(notificationId);
        if (n != null && n.getIsRead() == 0) {
            n.setIsRead(1);
            notificationMapper.updateById(n);
        }
    }

    @Override
    public void batchMarkAsRead(NotificationBatchReadDTO dto) {
        if (CollectionUtils.isEmpty(dto.getNotificationIds())) return;
        for (Long id : dto.getNotificationIds()) {
            markAsRead(id);
        }
    }

    @Override
    public void batchDelete(NotificationBatchDeleteDTO dto) {
        if (CollectionUtils.isEmpty(dto.getNotificationIds())) return;
        for (Long id : dto.getNotificationIds()) {
            notificationMapper.deleteById(id);
        }
    }

    @Override
    public void deleteNotification(Long notificationId) {
        notificationMapper.deleteById(notificationId);
    }

    @Override
    public NotificationUnreadCountVO getUnreadCount(Long userId) {
        List<NotificationEntity> all = notificationMapper.selectList(null);
        long count = all.stream().filter(n -> Objects.equals(n.getUserId(), userId) && n.getIsRead() == 0).count();
        NotificationUnreadCountVO vo = new NotificationUnreadCountVO();
        vo.setCount((int) count);
        return vo;
    }

    @Override
    public void sendEmailNotification(NotificationEmailDTO dto) {
        // Mock邮件发送
        log.info("[Mock] 发送邮件通知: subject={}, content={}, userIds={}", dto.getSubject(), dto.getContent(), dto.getUserIds());
        // 如需保存到系统通知
        if (Boolean.TRUE.equals(dto.getSaveToSystem())) {
            if (CollectionUtils.isEmpty(dto.getUserIds())) {
                // 查询所有用户ID（此处省略，实际应通过userFeignClient获取）
                return;
            }
            for (Long userId : dto.getUserIds()) {
                NotificationCreateDTO createDTO = new NotificationCreateDTO();
                createDTO.setUserId(userId);
                createDTO.setTitle(dto.getSubject());
                createDTO.setContent(dto.getContent());
                createDTO.setType(1); // 系统通知
                addNotification(createDTO);
            }
        }
    }
} 