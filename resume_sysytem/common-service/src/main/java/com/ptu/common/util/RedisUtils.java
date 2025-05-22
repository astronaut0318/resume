package com.ptu.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis工具类
 */
@Slf4j
@Component
public class RedisUtils {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     */
    public void expire(String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            log.error("设置缓存过期时间失败", e);
        }
    }

    /**
     * 获取过期时间
     *
     * @param key 键
     * @return 时间(秒) 返回0代表永久有效
     */
    public long getExpire(String key) {
        Long expire = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        return expire == null ? 0 : expire;
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true存在 false不存在
     */
    public boolean hasKey(String key) {
        Boolean hasKey = redisTemplate.hasKey(key);
        return hasKey != null && hasKey;
    }

    /**
     * 删除缓存
     *
     * @param key 键（一个或多个）
     */
    public void delete(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete((Collection<String>) Arrays.asList(key));
            }
        }
    }

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     */
    public void set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
        } catch (Exception e) {
            log.error("设置缓存失败", e);
        }
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     */
    public void set(String key, Object value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
        } catch (Exception e) {
            log.error("设置缓存失败", e);
        }
    }

    /**
     * 递增
     *
     * @param key   键
     * @param delta 递增因子(大于0)
     * @return 递增后的值
     */
    public long incr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        Long increment = redisTemplate.opsForValue().increment(key, delta);
        return increment == null ? 0 : increment;
    }

    /**
     * 递减
     *
     * @param key   键
     * @param delta 递减因子(大于0)
     * @return 递减后的值
     */
    public long decr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        Long decrement = redisTemplate.opsForValue().decrement(key, delta);
        return decrement == null ? 0 : decrement;
    }

    /**
     * 获取Hash结构中的属性
     *
     * @param key     键
     * @param hashKey Hash键
     * @return Hash值
     */
    public Object hGet(String key, String hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }

    /**
     * 获取Hash结构
     *
     * @param key 键
     * @return Hash结构
     */
    public Map<Object, Object> hGetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 设置Hash结构
     *
     * @param key 键
     * @param map Hash键值对
     */
    public void hPutAll(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
        } catch (Exception e) {
            log.error("设置Hash结构失败", e);
        }
    }

    /**
     * 设置Hash结构并设置时间
     *
     * @param key  键
     * @param map  Hash键值对
     * @param time 时间(秒)
     */
    public void hPutAll(String key, Map<String, Object> map, long time) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if (time > 0) {
                expire(key, time);
            }
        } catch (Exception e) {
            log.error("设置Hash结构失败", e);
        }
    }

    /**
     * 设置Hash结构中的属性
     *
     * @param key     键
     * @param hashKey Hash键
     * @param value   值
     */
    public void hPut(String key, String hashKey, Object value) {
        try {
            redisTemplate.opsForHash().put(key, hashKey, value);
        } catch (Exception e) {
            log.error("设置Hash属性失败", e);
        }
    }

    /**
     * 设置Hash结构中的属性并设置时间
     *
     * @param key     键
     * @param hashKey Hash键
     * @param value   值
     * @param time    时间(秒)
     */
    public void hPut(String key, String hashKey, Object value, long time) {
        try {
            redisTemplate.opsForHash().put(key, hashKey, value);
            if (time > 0) {
                expire(key, time);
            }
        } catch (Exception e) {
            log.error("设置Hash属性失败", e);
        }
    }

    /**
     * 删除Hash结构中的属性
     *
     * @param key      键
     * @param hashKeys Hash键
     */
    public void hDelete(String key, Object... hashKeys) {
        redisTemplate.opsForHash().delete(key, hashKeys);
    }

    /**
     * 判断Hash结构中是否有该属性
     *
     * @param key     键
     * @param hashKey Hash键
     * @return true存在 false不存在
     */
    public boolean hHasKey(String key, String hashKey) {
        return redisTemplate.opsForHash().hasKey(key, hashKey);
    }

    /**
     * 获取Set结构
     *
     * @param key 键
     * @return Set结构
     */
    public Set<Object> sGet(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            log.error("获取Set结构失败", e);
            return null;
        }
    }

    /**
     * 向Set结构中添加属性
     *
     * @param key    键
     * @param values 值（一个或多个）
     * @return 添加成功的个数
     */
    public long sAdd(String key, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            return count == null ? 0 : count;
        } catch (Exception e) {
            log.error("向Set结构中添加属性失败", e);
            return 0;
        }
    }

    /**
     * 向Set结构中添加属性并设置时间
     *
     * @param key    键
     * @param time   时间(秒)
     * @param values 值（一个或多个）
     * @return 添加成功的个数
     */
    public long sAdd(String key, long time, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            if (time > 0) {
                expire(key, time);
            }
            return count == null ? 0 : count;
        } catch (Exception e) {
            log.error("向Set结构中添加属性失败", e);
            return 0;
        }
    }

    /**
     * 判断Set结构中是否有该属性
     *
     * @param key   键
     * @param value 值
     * @return true存在 false不存在
     */
    public boolean sHasValue(String key, Object value) {
        try {
            Boolean isMember = redisTemplate.opsForSet().isMember(key, value);
            return isMember != null && isMember;
        } catch (Exception e) {
            log.error("判断Set结构中是否有该属性失败", e);
            return false;
        }
    }

    /**
     * 获取Set结构的长度
     *
     * @param key 键
     * @return 长度
     */
    public long sGetSize(String key) {
        try {
            Long size = redisTemplate.opsForSet().size(key);
            return size == null ? 0 : size;
        } catch (Exception e) {
            log.error("获取Set结构的长度失败", e);
            return 0;
        }
    }

    /**
     * 删除Set结构中的属性
     *
     * @param key    键
     * @param values 值（一个或多个）
     * @return 移除成功的个数
     */
    public long sRemove(String key, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().remove(key, values);
            return count == null ? 0 : count;
        } catch (Exception e) {
            log.error("删除Set结构中的属性失败", e);
            return 0;
        }
    }

    /**
     * 获取List结构中的属性
     *
     * @param key   键
     * @param start 开始索引
     * @param end   结束索引 0到-1代表所有值
     * @return List结构
     */
    public List<Object> lGet(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            log.error("获取List结构中的属性失败", e);
            return null;
        }
    }

    /**
     * 获取List结构的长度
     *
     * @param key 键
     * @return 长度
     */
    public long lGetSize(String key) {
        try {
            Long size = redisTemplate.opsForList().size(key);
            return size == null ? 0 : size;
        } catch (Exception e) {
            log.error("获取List结构的长度失败", e);
            return 0;
        }
    }

    /**
     * 通过索引获取List结构中的属性
     *
     * @param key   键
     * @param index 索引 index>=0时，0表头，1第二个元素，依次类推；index<0时，-1表尾，-2倒数第二个元素，依次类推
     * @return 值
     */
    public Object lGetIndex(String key, long index) {
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            log.error("通过索引获取List结构中的属性失败", e);
            return null;
        }
    }

    /**
     * 将值放入List结构尾部
     *
     * @param key   键
     * @param value 值
     */
    public void lPush(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
        } catch (Exception e) {
            log.error("将值放入List结构尾部失败", e);
        }
    }

    /**
     * 将值放入List结构尾部并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     */
    public void lPush(String key, Object value, long time) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            if (time > 0) {
                expire(key, time);
            }
        } catch (Exception e) {
            log.error("将值放入List结构尾部失败", e);
        }
    }

    /**
     * 将List放入List结构尾部
     *
     * @param key   键
     * @param value 值
     */
    public void lPushAll(String key, List<Object> value) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
        } catch (Exception e) {
            log.error("将List放入List结构尾部失败", e);
        }
    }

    /**
     * 将List放入List结构尾部并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     */
    public void lPushAll(String key, List<Object> value, long time) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            if (time > 0) {
                expire(key, time);
            }
        } catch (Exception e) {
            log.error("将List放入List结构尾部失败", e);
        }
    }

    /**
     * 修改List结构中的属性
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     */
    public void lUpdateIndex(String key, long index, Object value) {
        try {
            redisTemplate.opsForList().set(key, index, value);
        } catch (Exception e) {
            log.error("修改List结构中的属性失败", e);
        }
    }

    /**
     * 从List结构中移除N个值为value的元素
     *
     * @param key   键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    public long lRemove(String key, long count, Object value) {
        try {
            Long remove = redisTemplate.opsForList().remove(key, count, value);
            return remove == null ? 0 : remove;
        } catch (Exception e) {
            log.error("从List结构中移除N个值为value的元素失败", e);
            return 0;
        }
    }
} 