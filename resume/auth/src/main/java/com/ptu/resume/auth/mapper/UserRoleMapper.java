package com.ptu.resume.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ptu.resume.auth.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户角色关联Mapper接口
 *
 * @author PTU
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {
} 