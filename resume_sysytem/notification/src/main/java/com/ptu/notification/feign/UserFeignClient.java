package com.ptu.notification.feign;

import com.ptu.common.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.stereotype.Component;

/**
 * 用户服务Feign客户端
 */
@FeignClient(name = "user-service", fallbackFactory = UserFeignClient.UserFeignFallbackFactory.class)
public interface UserFeignClient {

    /**
     * 获取用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    @GetMapping("/api/users/{userId}")
    R<Object> getUserById(@PathVariable("userId") Long userId);

    @Component
    class UserFeignFallbackFactory implements org.springframework.cloud.openfeign.FallbackFactory<UserFeignClient> {
        @Override
        public UserFeignClient create(Throwable cause) {
            return userId -> R.failed("用户服务不可用");
        }
    }
} 