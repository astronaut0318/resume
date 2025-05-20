package com.ptu.resume.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ptu.resume.auth.entity.Role;
import com.ptu.resume.auth.vo.RoleVO;

import java.util.List;

/**
 * 角色服务接口
 *
 * @author PTU
 */
public interface RoleService extends IService<Role> {

    /**
     * 获取用户角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    List<RoleVO> getRolesByUserId(Long userId);

    /**
     * 获取所有角色列表
     *
     * @return 角色列表
     */
    List<RoleVO> getAllRoles();

    /**
     * 给用户分配角色
     *
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     * @return 是否成功
     */
    boolean assignRoles(Long userId, List<Long> roleIds);
}