package com.ptu.resume.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ptu.resume.auth.entity.RolePermission;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色权限关联Mapper接口
 *
 * @author PTU
 */
@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermission> {
} 