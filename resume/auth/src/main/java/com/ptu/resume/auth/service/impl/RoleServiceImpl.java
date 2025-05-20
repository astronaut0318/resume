package com.ptu.resume.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ptu.resume.auth.entity.Role;
import com.ptu.resume.auth.entity.UserRole;
import com.ptu.resume.auth.mapper.RoleMapper;
import com.ptu.resume.auth.mapper.UserRoleMapper;
import com.ptu.resume.auth.service.RoleService;
import com.ptu.resume.auth.vo.RoleVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色服务实现类
 *
 * @author PTU
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;

    /**
     * 获取用户角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    @Override
    public List<RoleVO> getRolesByUserId(Long userId) {
        List<Role> roles = roleMapper.selectRolesByUserId(userId);
        return roles.stream()
                .map(this::convertToRoleVO)
                .collect(Collectors.toList());
    }

    /**
     * 获取所有角色列表
     *
     * @return 角色列表
     */
    @Override
    public List<RoleVO> getAllRoles() {
        // 查询状态正常的角色
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getStatus, 0);
        wrapper.orderByAsc(Role::getSort);
        List<Role> roles = list(wrapper);
        return roles.stream()
                .map(this::convertToRoleVO)
                .collect(Collectors.toList());
    }

    /**
     * 给用户分配角色
     *
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignRoles(Long userId, List<Long> roleIds) {
        // 先删除用户原有角色
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId, userId);
        userRoleMapper.delete(wrapper);
        
        // 分配新角色
        if (roleIds != null && !roleIds.isEmpty()) {
            List<UserRole> userRoles = new ArrayList<>();
            for (Long roleId : roleIds) {
                UserRole userRole = new UserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                userRoles.add(userRole);
            }
            // 批量插入
            return userRoles.stream()
                    .map(userRole -> userRoleMapper.insert(userRole) > 0)
                    .reduce(Boolean::logicalAnd)
                    .orElse(true);
        }
        return true;
    }

    /**
     * 将Role转换为RoleVO
     *
     * @param role 角色实体
     * @return 角色VO
     */
    private RoleVO convertToRoleVO(Role role) {
        if (role == null) {
            return null;
        }
        RoleVO roleVO = new RoleVO();
        BeanUtils.copyProperties(role, roleVO);
        return roleVO;
    }
}