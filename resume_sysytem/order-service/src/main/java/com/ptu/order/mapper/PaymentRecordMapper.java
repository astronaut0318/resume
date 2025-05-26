package com.ptu.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ptu.order.entity.PaymentRecordEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付记录表Mapper接口
 */
@Mapper
public interface PaymentRecordMapper extends BaseMapper<PaymentRecordEntity> {
} 