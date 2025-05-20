package com.ptu.resume.service.redis;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis服务接口
 *
 * @author PTU开发团队
 */
public interface RedisService {
    
    /**
     * 缓存值
     *
     * @param key   键
     * @param value 值
     */
    void set(String key, Object value);
    
    /**
     * 缓存值并设置过期时间
     *
     * @param key     键
     * @param value   值
     * @param timeout 过期时间（秒）
     */
    void set(String key, Object value, long timeout);
    
    /**
     * 缓存值并设置过期时间
     *
     * @param key      键
     * @param value    值
     * @param timeout  过期时间
     * @param timeUnit 时间单位
     */
    void set(String key, Object value, long timeout, TimeUnit timeUnit);
    
    /**
     * 获取缓存值
     *
     * @param key 键
     * @param <T> 值类型
     * @return 值
     */
    <T> T get(String key);
    
    /**
     * 删除缓存
     *
     * @param key 键
     * @return 是否删除成功
     */
    Boolean delete(String key);
    
    /**
     * 批量删除缓存
     *
     * @param keys 键集合
     * @return 删除的数量
     */
    Long delete(Collection<String> keys);
    
    /**
     * 是否存在key
     *
     * @param key 键
     * @return 是否存在
     */
    Boolean hasKey(String key);
    
    /**
     * 设置过期时间
     *
     * @param key     键
     * @param timeout 过期时间（秒）
     * @return 是否设置成功
     */
    Boolean expire(String key, long timeout);
    
    /**
     * 设置过期时间
     *
     * @param key      键
     * @param timeout  过期时间
     * @param timeUnit 时间单位
     * @return 是否设置成功
     */
    Boolean expire(String key, long timeout, TimeUnit timeUnit);
    
    /**
     * 获取过期时间
     *
     * @param key 键
     * @return 过期时间（秒）
     */
    Long getExpire(String key);
    
    /**
     * 递增
     *
     * @param key   键
     * @param delta 递增值
     * @return 递增后的值
     */
    Long increment(String key, long delta);
    
    /**
     * 递减
     *
     * @param key   键
     * @param delta 递减值
     * @return 递减后的值
     */
    Long decrement(String key, long delta);
    
    /**
     * 获取Hash结构中的所有键值对
     *
     * @param key 键
     * @return 键值对集合
     */
    Map<Object, Object> hGetAll(String key);
    
    /**
     * 向Hash结构中放入一个键值对
     *
     * @param key   键
     * @param field Hash字段
     * @param value 值
     */
    void hSet(String key, String field, Object value);
    
    /**
     * 向Hash结构中放入一个键值对，并设置过期时间
     *
     * @param key     键
     * @param field   Hash字段
     * @param value   值
     * @param timeout 过期时间（秒）
     */
    void hSet(String key, String field, Object value, long timeout);
    
    /**
     * 获取Hash结构中的指定字段值
     *
     * @param key   键
     * @param field Hash字段
     * @param <T>   值类型
     * @return 值
     */
    <T> T hGet(String key, String field);
    
    /**
     * 删除Hash结构中的指定字段
     *
     * @param key    键
     * @param fields Hash字段
     * @return 删除的数量
     */
    Long hDelete(String key, Object... fields);
    
    /**
     * 判断Hash结构中是否存在指定字段
     *
     * @param key   键
     * @param field Hash字段
     * @return 是否存在
     */
    Boolean hHasKey(String key, String field);
    
    /**
     * 将List结构中的所有元素按从左到右的顺序返回
     *
     * @param key 键
     * @param <T> 元素类型
     * @return 元素集合
     */
    <T> List<T> lRange(String key);
    
    /**
     * 将元素从左边放入List结构
     *
     * @param key   键
     * @param value 值
     * @return List结构的长度
     */
    Long lLeftPush(String key, Object value);
    
    /**
     * 将元素从右边放入List结构
     *
     * @param key   键
     * @param value 值
     * @return List结构的长度
     */
    Long lRightPush(String key, Object value);
    
    /**
     * 将元素从左边取出List结构
     *
     * @param key 键
     * @param <T> 元素类型
     * @return 元素
     */
    <T> T lLeftPop(String key);
    
    /**
     * 将元素从右边取出List结构
     *
     * @param key 键
     * @param <T> 元素类型
     * @return 元素
     */
    <T> T lRightPop(String key);
    
    /**
     * 向Set结构中添加元素
     *
     * @param key    键
     * @param values 值
     * @return 添加的数量
     */
    Long sAdd(String key, Object... values);
    
    /**
     * 获取Set结构中的所有元素
     *
     * @param key 键
     * @param <T> 元素类型
     * @return 元素集合
     */
    <T> Set<T> sMembers(String key);
    
    /**
     * 判断Set结构中是否存在指定元素
     *
     * @param key   键
     * @param value 值
     * @return 是否存在
     */
    Boolean sIsMember(String key, Object value);
    
    /**
     * 获取Set结构的长度
     *
     * @param key 键
     * @return Set结构的长度
     */
    Long sSize(String key);
    
    /**
     * 从Set结构中移除指定元素
     *
     * @param key    键
     * @param values 值
     * @return 移除的数量
     */
    Long sRemove(String key, Object... values);
    
    /**
     * 向ZSet结构中添加元素
     *
     * @param key   键
     * @param value 值
     * @param score 分数
     * @return 是否添加成功
     */
    Boolean zAdd(String key, Object value, double score);
    
    /**
     * 获取ZSet结构中指定分数范围的元素
     *
     * @param key 键
     * @param min 最小分数
     * @param max 最大分数
     * @param <T> 元素类型
     * @return 元素集合
     */
    <T> Set<T> zRangeByScore(String key, double min, double max);
    
    /**
     * 获取ZSet结构中指定元素的分数
     *
     * @param key   键
     * @param value 值
     * @return 分数
     */
    Double zScore(String key, Object value);
    
    /**
     * 移除ZSet结构中的指定元素
     *
     * @param key    键
     * @param values 值
     * @return 移除的数量
     */
    Long zRemove(String key, Object... values);
} 