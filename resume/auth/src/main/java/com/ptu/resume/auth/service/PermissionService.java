package com.ptu.resume.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ptu.resume.auth.entity.Permission;

import java.util.List;

/**
 * 权限服务接口
 *
 * @author PTU
 */
public interface PermissionService extends IService<Permission> {

    /**
     * 获取角色权限列表
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    List<Permission> getPermissionsByRoleId(Long roleId);

    /**
     * 获取用户权限列表
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    List<Permission> getPermissionsByUserId(Long userId);

    /**
     * 给角色分配权限
     *
     * @param roleId 角色ID
     * @param permissionIds 权限ID列表
     * @return 是否成功
     */
    boolean assignPermissions(Long roleId, List<Long> permissionIds);
}