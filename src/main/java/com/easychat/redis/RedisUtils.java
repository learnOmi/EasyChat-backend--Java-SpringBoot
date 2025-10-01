package com.easychat.redis;

import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component("redisUtils")
public class RedisUtils<V> {
    @Resource
    private RedisTemplate<String, V> redisTemplate;
    private static final Logger logger = LoggerFactory.getLogger(RedisUtils.class);

    public boolean set(String key, V value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            logger.error("设置redisKey:{}, value:{}失败", key, value);
            return false;
        }
    }

    public V get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    public void delete(String key) {
        if (key != null && key.length() > 0) {
            if (key.length() == 1) {
                redisTemplate.delete(key);
            } else {
                redisTemplate.delete((Collection<String>) CollectionUtils.arrayToList(key));
            }
        }
    }

    public boolean setex(String key, V value, long expire) {
        try {
            if (expire > 0) {
                redisTemplate.opsForValue().set(key, value, expire, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            logger.error("设置redisKey:{}, value:{}失败", key, value);
            return false;
        }
    }

    public boolean expire(String key, long expire) {
        try {
            if (expire > 0) {
                redisTemplate.expire(key, expire, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            logger.error("设置redisKey:过期时间失败", key);
            return false;
        }
    }

    public List<V> getQueueList(String key) {
        return redisTemplate.opsForList().range(key, 0, -1);
    }

    public boolean lpush(String key, V value, long time) {
        try {
            redisTemplate.opsForList().leftPush(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            logger.error("设置redisKey:{}, value:{}失败", key, value);
            return false;
        }
    }

    public boolean lpushAll(String key, List<V> list, long time) {
        try {
            redisTemplate.opsForList().leftPushAll(key, list);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            logger.error("设置redisKey:, value:失败", key, list);
            return false;
        }
    }

    public long remove(String key, Objects value) {
        try {
            Long remove = redisTemplate.opsForList().remove(key, 0, value);
            return remove;
        } catch (Exception e) {
            logger.error("设置redisKey:{}, value:{}失败", key, value);
            return 0;
        }
    }

}
