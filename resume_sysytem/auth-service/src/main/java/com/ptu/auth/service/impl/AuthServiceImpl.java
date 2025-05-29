package com.ptu.auth.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptu.auth.dto.ChangePasswordDTO;
import com.ptu.auth.dto.LoginDTO;
import com.ptu.auth.dto.RefreshTokenDTO;
import com.ptu.auth.entity.UserVO;
import com.ptu.auth.feign.UserFeignClient;
import com.ptu.auth.service.AuthService;
import com.ptu.auth.util.JwtTokenUtil;
import com.ptu.auth.vo.TokenVO;
import com.ptu.common.api.R;
import com.ptu.common.exception.BusinessException;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * 认证服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserFeignClient userFeignClient;
    private final JwtTokenUtil jwtTokenUtil;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Redis中存储黑名单令牌的前缀
     */
    private static final String TOKEN_BLACKLIST_PREFIX = "token:blacklist:";

    /**
     * Redis中存储刷新令牌的前缀
     */
    private static final String REFRESH_TOKEN_PREFIX = "token:refresh:";

    /**
     * 登录
     *
     * @param loginDTO 登录参数
     * @return 令牌信息
     */
    @Override
    public TokenVO login(LoginDTO loginDTO) {
        // 远程调用user-service获取用户信息
        R<?> result = userFeignClient.getUserByUsername(loginDTO.getUsername());
        if (!result.isSuccess()) {
            throw new BusinessException(result.getMessage());
        }

        // 将结果转换为UserVO
        UserVO user = objectMapper.convertValue(result.getData(), new TypeReference<UserVO>() {});
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 检查账号状态
        if (user.getStatus() != 1) {
            throw new BusinessException("账号已禁用");
        }

        // 验证密码
        String encryptedPassword = DigestUtils.md5DigestAsHex(loginDTO.getPassword().getBytes(StandardCharsets.UTF_8));
        if (!encryptedPassword.equals(user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 生成访问令牌和刷新令牌
        String accessToken = jwtTokenUtil.generateAccessToken(user.getId(), user.getUsername(), user.getRole());
        String refreshToken = jwtTokenUtil.generateRefreshToken(user.getId());

        // 将accessToken存入Redis，key为token:{userId}，value为accessToken
        redisTemplate.opsForValue().set(
            "token:" + user.getId(),
            accessToken,
            jwtTokenUtil.getExpirationDateFromToken(accessToken).getTime() - System.currentTimeMillis(),
            TimeUnit.MILLISECONDS
        );

        // 将刷新令牌存入Redis
        redisTemplate.opsForValue().set(
                REFRESH_TOKEN_PREFIX + user.getId(),
                refreshToken,
                jwtTokenUtil.getExpirationDateFromToken(refreshToken).getTime() - System.currentTimeMillis(),
                TimeUnit.MILLISECONDS
        );

        // 构建并返回令牌信息
        return new TokenVO()
                .setUserId(user.getId())
                .setUsername(user.getUsername())
                .setToken(accessToken)
                .setRefreshToken(refreshToken)
                .setExpiresIn(jwtTokenUtil.getExpirationDateFromToken(accessToken).getTime() - System.currentTimeMillis());
    }

    /**
     * 刷新令牌
     *
     * @param refreshTokenDTO 刷新令牌参数
     * @return 令牌信息
     */
    @Override
    public TokenVO refresh(RefreshTokenDTO refreshTokenDTO) {
        String refreshToken = refreshTokenDTO.getRefreshToken();
        
        // 验证刷新令牌
        try {
            // 检查是否是合法的JWT
            if (!jwtTokenUtil.getClaimFromToken(refreshToken, claims -> claims.get("type", String.class)).equals("refresh")) {
                throw new BusinessException("无效的刷新令牌");
            }

            // 获取userId
            Long userId = jwtTokenUtil.getUserIdFromToken(refreshToken);

            // 从Redis中获取存储的刷新令牌
            String storedRefreshToken = (String) redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + userId);
            if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
                throw new BusinessException("刷新令牌已失效");
            }

            // 远程调用user-service获取用户信息
            R<?> result = userFeignClient.getUserByUsername(jwtTokenUtil.getUsernameFromToken(refreshToken));
            if (!result.isSuccess()) {
                throw new BusinessException(result.getMessage());
            }

            // 将结果转换为UserVO
            UserVO user = objectMapper.convertValue(result.getData(), new TypeReference<UserVO>() {});
            if (user == null) {
                throw new BusinessException("用户不存在");
            }

            // 生成新的访问令牌和刷新令牌
            String newAccessToken = jwtTokenUtil.generateAccessToken(user.getId(), user.getUsername(), user.getRole());
            String newRefreshToken = jwtTokenUtil.generateRefreshToken(user.getId());

            // 将旧的刷新令牌加入黑名单
            redisTemplate.opsForValue().set(
                    TOKEN_BLACKLIST_PREFIX + refreshToken,
                    "1",
                    jwtTokenUtil.getExpirationDateFromToken(refreshToken).getTime() - System.currentTimeMillis(),
                    TimeUnit.MILLISECONDS
            );

            // 将新的刷新令牌存入Redis
            redisTemplate.opsForValue().set(
                    REFRESH_TOKEN_PREFIX + user.getId(),
                    newRefreshToken,
                    jwtTokenUtil.getExpirationDateFromToken(newRefreshToken).getTime() - System.currentTimeMillis(),
                    TimeUnit.MILLISECONDS
            );

            // 构建并返回令牌信息
            return new TokenVO()
                    .setUserId(user.getId())
                    .setUsername(user.getUsername())
                    .setToken(newAccessToken)
                    .setRefreshToken(newRefreshToken)
                    .setExpiresIn(jwtTokenUtil.getExpirationDateFromToken(newAccessToken).getTime() - System.currentTimeMillis());
        } catch (ExpiredJwtException e) {
            throw new BusinessException("刷新令牌已过期");
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("刷新令牌失败", e);
            throw new BusinessException("刷新令牌失败");
        }
    }

    /**
     * 登出
     *
     * @param userId 用户ID
     * @return 是否成功
     */
    @Override
    public boolean logout(Long userId) {
        // 从Redis中删除刷新令牌
        redisTemplate.delete(REFRESH_TOKEN_PREFIX + userId);
        return true;
    }

    /**
     * 修改密码
     *
     * @param userId           用户ID
     * @param changePasswordDTO 修改密码参数
     * @return 是否成功
     */
    @Override
    public boolean changePassword(Long userId, ChangePasswordDTO changePasswordDTO) {
        try {
            // 验证旧密码
            R<Boolean> verifyResult = userFeignClient.verifyPassword(userId, changePasswordDTO.getOldPassword());
            if (!verifyResult.isSuccess() || !Boolean.TRUE.equals(verifyResult.getData())) {
                throw new BusinessException("旧密码不正确");
            }
            
            // 更新密码
            R<Boolean> updateResult = userFeignClient.updatePassword(userId, changePasswordDTO.getNewPassword());
            if (!updateResult.isSuccess() || !Boolean.TRUE.equals(updateResult.getData())) {
                throw new BusinessException("修改密码失败");
            }
            
            // 登出用户（使当前令牌失效）
            logout(userId);
            
            return true;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("修改密码失败", e);
            throw new BusinessException("修改密码失败");
        }
    }
} 