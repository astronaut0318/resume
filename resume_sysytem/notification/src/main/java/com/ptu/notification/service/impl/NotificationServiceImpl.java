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
        try {
            // 校验参数
            if (dto == null) {
                throw new IllegalArgumentException("通知数据不能为空");
            }
            
            if (dto.getUserId() == null) {
                throw new IllegalArgumentException("用户ID不能为空");
            }
            
            if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
                throw new IllegalArgumentException("通知标题不能为空");
            }
            
            if (dto.getContent() == null || dto.getContent().trim().isEmpty()) {
                throw new IllegalArgumentException("通知内容不能为空");
            }
            
            if (dto.getType() == null) {
                // 默认为系统通知
                dto.setType(1);
            }
            
            // 尝试校验用户存在，但不阻止通知创建
            try {
                R<Object> userResp = userFeignClient.getUserById(dto.getUserId());
                if (userResp == null || userResp.getCode() != 200) {
                    log.warn("创建通知时用户可能不存在，但将继续创建通知, userId={}", dto.getUserId());
                }
            } catch (Exception e) {
                log.warn("调用用户服务异常，但将继续创建通知, userId={}, error={}", dto.getUserId(), e.getMessage());
                // 忽略异常，允许继续创建通知
            }
            
            // 创建并保存通知实体
            NotificationEntity entity = new NotificationEntity();
            BeanUtils.copyProperties(dto, entity);
            entity.setIsRead(0); // 设置为未读
            entity.setCreateTime(new Date());
            
            log.info("创建新通知: userId={}, title={}, type={}", dto.getUserId(), dto.getTitle(), dto.getType());
            notificationMapper.insert(entity);
            log.debug("通知创建成功: id={}", entity.getId());
        } catch (Exception e) {
            log.error("创建通知失败", e);
            throw e;
        }
    }

    @Override
    public List<NotificationVO> listNotifications(Long userId, Integer isRead, Integer type, int page, int size) {
        try {
            log.debug("查询通知列表: userId={}, isRead={}, type={}, page={}, size={}", userId, isRead, type, page, size);
            
            if (userId == null) {
                log.warn("查询通知时用户ID为空");
                return Collections.emptyList();
            }
            
            // 使用MyBatis Plus条件构造器
            com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<NotificationEntity> queryWrapper = 
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
            
            // 设置查询条件
            queryWrapper.eq("user_id", userId);
            if (isRead != null) {
                queryWrapper.eq("is_read", isRead);
            }
            if (type != null) {
                queryWrapper.eq("type", type);
            }
            
            // 按创建时间倒序排序，使最新通知在前
            queryWrapper.orderByDesc("create_time");
            
            // 创建分页对象
            com.baomidou.mybatisplus.extension.plugins.pagination.Page<NotificationEntity> pageParam = 
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size);
            
            // 执行分页查询
            com.baomidou.mybatisplus.extension.plugins.pagination.Page<NotificationEntity> resultPage = 
                notificationMapper.selectPage(pageParam, queryWrapper);
            
            // 转换为VO对象
            List<NotificationVO> result = resultPage.getRecords().stream()
                .map(entity -> {
                    NotificationVO vo = new NotificationVO();
                    BeanUtils.copyProperties(entity, vo);
                    return vo;
                })
                .collect(Collectors.toList());
            
            log.debug("查询到{}条通知记录", result.size());
            return result;
        } catch (Exception e) {
            log.error("查询通知列表失败", e);
            return Collections.emptyList();
        }
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
        if (CollectionUtils.isEmpty(dto.getNotificationIds())) {
            log.warn("批量标记已读通知ID列表为空");
            return;
        }
        
        log.info("批量标记通知为已读: ids={}", dto.getNotificationIds());
        
        try {
            // 优化：使用批量更新而不是循环单个更新
            List<NotificationEntity> entities = notificationMapper.selectBatchIds(dto.getNotificationIds());
            if (CollectionUtils.isEmpty(entities)) {
                log.warn("未找到指定通知: ids={}", dto.getNotificationIds());
                return;
            }
            
            // 过滤出未读的通知
            List<NotificationEntity> unreadEntities = entities.stream()
                    .filter(e -> e.getIsRead() == 0)
                    .collect(Collectors.toList());
            
            if (CollectionUtils.isEmpty(unreadEntities)) {
                log.info("所有通知已为已读状态，无需更新");
                return;
            }
            
            // 更新为已读
            for (NotificationEntity entity : unreadEntities) {
                entity.setIsRead(1);
                notificationMapper.updateById(entity);
            }
            
            log.info("已将{}条通知标记为已读", unreadEntities.size());
        } catch (Exception e) {
            log.error("批量标记通知为已读失败", e);
            throw new RuntimeException("批量标记通知为已读失败", e);
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
        // 优化：直接使用条件查询，而不是获取全部后过滤
        NotificationUnreadCountVO vo = new NotificationUnreadCountVO();
        try {
            if (userId == null) {
                log.warn("获取未读通知数量时用户ID为空");
                vo.setCount(0);
                return vo;
            }
            
            com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<NotificationEntity> queryWrapper = 
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
            queryWrapper.eq("user_id", userId)
                       .eq("is_read", 0);
            
            Integer count = Math.toIntExact(notificationMapper.selectCount(queryWrapper));
            vo.setCount(count);
            
            log.debug("用户{}未读通知数量: {}", userId, count);
            return vo;
        } catch (Exception e) {
            log.error("获取未读通知数量失败", e);
            vo.setCount(0);
            return vo;
        }
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