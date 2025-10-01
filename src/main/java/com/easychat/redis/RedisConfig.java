package com.easychat.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * Redis配置类，用于配置RedisTemplate实例
 * 该类提供了Redis连接和序列化的配置，使Spring应用能够方便地操作Redis
 *
 * @param <V> Redis值类型的泛型参数
 */
@Configuration
public class RedisConfig<V> {
    /**
     * 创建并配置RedisTemplate Bean
     *
     * @param redisConnectionFactory Redis连接工厂，用于建立与Redis的连接
     * @return 配置好的RedisTemplate实例，支持String类型的键和V类型的值
     */
    @Bean("redisTemplate")
    public RedisTemplate<String, V> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        // 创建RedisTemplate实例
        RedisTemplate<String, V> redisTemplate = new RedisTemplate<>();
        // 设置Redis连接工厂
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        // 设置键的序列化方式为String序列化
        redisTemplate.setKeySerializer(RedisSerializer.string());
        // 设置值的序列化方式为JSON序列化
        redisTemplate.setValueSerializer(RedisSerializer.json());
        // 设置哈希结构的键的序列化方式为String序列化
        redisTemplate.setHashKeySerializer(RedisSerializer.string());
        // 设置哈希结构的值的序列化方式为JSON序列化
        redisTemplate.setHashValueSerializer(RedisSerializer.json());
        // 初始化RedisTemplate，完成配置
        redisTemplate.afterPropertiesSet();
        // 返回配置完成的RedisTemplate
        return redisTemplate;
    }

}
