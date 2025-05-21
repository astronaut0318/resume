package com.ptu.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ptu.user.entity.UserVipEntity;
import com.ptu.user.vo.UserVipVO;

/**
 * 用户VIP会员服务接口
 */
public interface UserVipService extends IService<UserVipEntity> {

    /**
     * 获取用户VIP会员信息
     *
     * @param userId 用户ID
     * @return VIP会员信息
     */
    UserVipVO getVipInfo(Long userId);

    /**
     * 为用户开通VIP会员
     *
     * @param userId 用户ID
     * @param level  VIP等级
     * @param months 开通月数
     * @return 是否成功
     */
    boolean openVip(Long userId, Integer level, Integer months);

    /**
     * 为用户续费VIP会员
     *
     * @param userId 用户ID
     * @param months 续费月数
     * @return 是否成功
     */
    boolean renewVip(Long userId, Integer months);

    /**
     * 升级VIP等级
     *
     * @param userId 用户ID
     * @param level  新的VIP等级
     * @return 是否成功
     */
    boolean upgradeVip(Long userId, Integer level);
} 