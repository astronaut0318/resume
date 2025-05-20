package com.ptu.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ptu.user.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户数据访问层
 */
@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {
    
} 