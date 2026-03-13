package com.easychat.redis;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
    private static final Logger logger = LoggerFactory.getLogger(RedisConfig.class);

    @Value("${spring.data.redis.host:}")
    private String redisHost;
    @Value("${spring.data.redis.port:}")
    private Integer redisPort;

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

/**
 * 创建并配置Redisson客户端Bean
 * 该方法使用Spring的@Bean注解将RedissonClient注册为Spring容器中的一个Bean
 * 设置了Bean的名称为"redissonClient"，并配置了销毁方法为"shutdown"
 *
 * @return 返回配置好的RedissonClient实例，如果配置失败则返回null
 */
    @Bean(name = "redissonClient", destroyMethod = "shutdown")
    public RedissonClient redissonClient(){
        try {
        // 创建Redisson配置对象
            Config config = new Config();
        // 配置使用单服务器模式，并设置Redis服务器的主机和端口
            config.useSingleServer().setAddress("redis://" + redisHost + ":" + redisPort);
        // 根据配置创建Redisson客户端实例
            RedissonClient redissonClient = Redisson.create(config);
        // 返回创建的客户端实例
            return redissonClient;
        } catch (Exception e) {
        // 捕获并处理可能出现的异常，打印错误日志
            logger.info("redis配置错误，请检查配置！");
        }
        // 如果出现异常，返回null
        return null;
    }

}
