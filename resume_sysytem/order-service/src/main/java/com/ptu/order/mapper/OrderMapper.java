package com.ptu.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ptu.order.entity.OrderEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单表Mapper接口
 */
@Mapper
public interface OrderMapper extends BaseMapper<OrderEntity> {
} 