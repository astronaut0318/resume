package com.ptu.notification.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ptu.notification.entity.NotificationEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 通知表Mapper接口
 */
@Mapper
public interface NotificationMapper extends BaseMapper<NotificationEntity> {
} 