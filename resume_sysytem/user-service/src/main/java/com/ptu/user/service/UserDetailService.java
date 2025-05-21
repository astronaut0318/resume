package com.ptu.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ptu.user.dto.UserDetailUpdateDTO;
import com.ptu.user.entity.UserDetailEntity;
import com.ptu.user.vo.UserDetailVO;

/**
 * 用户详细信息服务接口
 */
public interface UserDetailService extends IService<UserDetailEntity> {

    /**
     * 根据用户ID获取用户详细信息
     *
     * @param userId 用户ID
     * @return 用户详细信息VO
     */
    UserDetailVO getDetailByUserId(Long userId);

    /**
     * 更新用户详细信息
     *
     * @param userId            用户ID
     * @param userDetailUpdateDTO 详细信息更新DTO
     * @return 是否成功
     */
    boolean updateDetail(Long userId, UserDetailUpdateDTO userDetailUpdateDTO);

    /**
     * 创建用户详细信息
     *
     * @param userId            用户ID
     * @param userDetailUpdateDTO 详细信息更新DTO
     * @return 是否成功
     */
    boolean createDetail(Long userId, UserDetailUpdateDTO userDetailUpdateDTO);
} 