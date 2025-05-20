package com.ptu.resume.core.constants;

/**
 * 系统通用常量
 *
 * @author PTU开发团队
 */
public interface CommonConstants {
    
    /**
     * 成功标记
     */
    Integer SUCCESS = 0;
    
    /**
     * 失败标记
     */
    Integer FAIL = 1;
    
    /**
     * 删除标记（0-正常，1-删除）
     */
    String DEL_FLAG_0 = "0";
    String DEL_FLAG_1 = "1";
    
    /**
     * 启用状态（0-禁用，1-启用）
     */
    String STATUS_0 = "0";
    String STATUS_1 = "1";
    
    /**
     * 验证码前缀
     */
    String CAPTCHA_PREFIX = "resume:captcha:";
    
    /**
     * 默认过期时间（单位：秒）
     */
    long DEFAULT_EXPIRE = 60 * 30;
    
    /**
     * 分页参数
     */
    String PAGE = "page";
    String LIMIT = "limit";
    String ORDER_FIELD = "orderField";
    String ORDER = "order";
    String ASC = "asc";
    
    /**
     * 用户角色
     */
    int ROLE_USER = 0;
    int ROLE_VIP = 1;
    int ROLE_ADMIN = 2;
} 