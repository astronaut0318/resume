package com.ptu.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ptu.common.exception.BusinessException;
import com.ptu.user.entity.UserEntity;
import com.ptu.user.entity.UserVipEntity;
import com.ptu.user.mapper.UserVipMapper;
import com.ptu.user.service.UserService;
import com.ptu.user.service.UserVipService;
import com.ptu.user.vo.UserVipVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 用户VIP会员服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserVipServiceImpl extends ServiceImpl<UserVipMapper, UserVipEntity> implements UserVipService {

    private final UserService userService;

    /**
     * 获取用户VIP会员信息
     *
     * @param userId 用户ID
     * @return VIP会员信息
     */
    @Override
    public UserVipVO getVipInfo(Long userId) {
        // 校验用户是否存在
        UserEntity userEntity = userService.getById(userId);
        if (userEntity == null) {
            return null;
        }

        // 查询VIP信息
        LambdaQueryWrapper<UserVipEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserVipEntity::getUserId, userId);
        UserVipEntity vipEntity = getOne(queryWrapper);

        // 构建VO对象
        UserVipVO vipVO = new UserVipVO();
        if (vipEntity != null) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime endTime = vipEntity.getEndTime();
            boolean isVip = endTime != null && endTime.isAfter(now);

            vipVO.setIsVip(isVip);
            vipVO.setLevel(isVip ? vipEntity.getLevel() : null);
            vipVO.setStartTime(vipEntity.getStartTime());
            vipVO.setEndTime(vipEntity.getEndTime());
            
            // 计算剩余天数
            if (isVip) {
                vipVO.setRemainingDays((int) ChronoUnit.DAYS.between(now, endTime));
            } else {
                vipVO.setRemainingDays(0);
            }
        } else {
            vipVO.setIsVip(false);
            vipVO.setRemainingDays(0);
        }

        return vipVO;
    }

    /**
     * 为用户开通VIP会员
     *
     * @param userId 用户ID
     * @param level  VIP等级
     * @param months 开通月数
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean openVip(Long userId, Integer level, Integer months) {
        // 校验参数
        if (level == null || level < 1 || level > 3) {
            throw new BusinessException("VIP等级不合法");
        }
        if (months == null || months < 1) {
            throw new BusinessException("开通月数不合法");
        }

        // 校验用户是否存在
        UserEntity userEntity = userService.getById(userId);
        if (userEntity == null) {
            throw new BusinessException("用户不存在");
        }

        // 查询用户是否已经是VIP
        LambdaQueryWrapper<UserVipEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserVipEntity::getUserId, userId);
        UserVipEntity vipEntity = getOne(queryWrapper);

        LocalDateTime now = LocalDateTime.now();
        
        // 如果用户已经是VIP但已过期，则需要重新开通
        if (vipEntity != null && vipEntity.getEndTime().isBefore(now)) {
            vipEntity.setLevel(level);
            vipEntity.setStartTime(now);
            vipEntity.setEndTime(now.plusMonths(months));
            vipEntity.setUpdateTime(now);
            return updateById(vipEntity);
        }
        
        // 如果用户已经是VIP且未过期，则不能重复开通
        if (vipEntity != null && vipEntity.getEndTime().isAfter(now)) {
            throw new BusinessException("用户已经是VIP会员，请选择续费或升级");
        }

        // 创建VIP实体
        vipEntity = new UserVipEntity();
        vipEntity.setUserId(userId);
        vipEntity.setLevel(level);
        vipEntity.setStartTime(now);
        vipEntity.setEndTime(now.plusMonths(months));
        vipEntity.setCreateTime(now);
        vipEntity.setUpdateTime(now);
        vipEntity.setDeleted(0);
        
        // 保存实体并修改用户角色为VIP用户
        boolean saveResult = save(vipEntity);
        if (saveResult) {
            userEntity.setRole(1); // 1-VIP用户
            userEntity.setUpdateTime(now);
            return userService.updateById(userEntity);
        }
        
        return false;
    }

    /**
     * 为用户续费VIP会员
     *
     * @param userId 用户ID
     * @param months 续费月数
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean renewVip(Long userId, Integer months) {
        // 校验参数
        if (months == null || months < 1) {
            throw new BusinessException("续费月数不合法");
        }

        // 校验用户是否存在
        UserEntity userEntity = userService.getById(userId);
        if (userEntity == null) {
            throw new BusinessException("用户不存在");
        }

        // 查询用户VIP信息
        LambdaQueryWrapper<UserVipEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserVipEntity::getUserId, userId);
        UserVipEntity vipEntity = getOne(queryWrapper);

        // 如果用户不是VIP，无法续费
        if (vipEntity == null) {
            throw new BusinessException("用户不是VIP会员，请先开通");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime newEndTime;
        
        // 如果已过期，则从当前时间开始计算
        if (vipEntity.getEndTime().isBefore(now)) {
            vipEntity.setStartTime(now);
            newEndTime = now.plusMonths(months);
        } else {
            // 如果未过期，则在原结束时间基础上延长
            newEndTime = vipEntity.getEndTime().plusMonths(months);
        }
        
        vipEntity.setEndTime(newEndTime);
        vipEntity.setUpdateTime(now);
        
        // 确保用户角色为VIP用户
        if (userEntity.getRole() != 1) {
            userEntity.setRole(1); // 1-VIP用户
            userEntity.setUpdateTime(now);
            userService.updateById(userEntity);
        }
        
        return updateById(vipEntity);
    }

    /**
     * 升级VIP等级
     *
     * @param userId 用户ID
     * @param level  新的VIP等级
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean upgradeVip(Long userId, Integer level) {
        // 校验参数
        if (level == null || level < 1 || level > 3) {
            throw new BusinessException("VIP等级不合法");
        }

        // 校验用户是否存在
        UserEntity userEntity = userService.getById(userId);
        if (userEntity == null) {
            throw new BusinessException("用户不存在");
        }

        // 查询用户VIP信息
        LambdaQueryWrapper<UserVipEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserVipEntity::getUserId, userId);
        UserVipEntity vipEntity = getOne(queryWrapper);

        // 如果用户不是VIP，无法升级
        if (vipEntity == null) {
            throw new BusinessException("用户不是VIP会员，请先开通");
        }

        // 如果VIP已过期，无法升级
        LocalDateTime now = LocalDateTime.now();
        if (vipEntity.getEndTime().isBefore(now)) {
            throw new BusinessException("VIP已过期，请先续费");
        }

        // 如果当前等级高于或等于要升级的等级，无法升级
        if (vipEntity.getLevel() >= level) {
            throw new BusinessException("无法升级到同级或更低级别");
        }

        // 更新VIP等级
        vipEntity.setLevel(level);
        vipEntity.setUpdateTime(now);
        
        return updateById(vipEntity);
    }
} 