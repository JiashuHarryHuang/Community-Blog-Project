package com.community_blog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;


@Configuration
public class RedisConfig {
    /**
     * 序列化器
     * @param factory Redis connection factory
     * @return redis template
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // 设置key的序列化方式
        template.setKeySerializer(RedisSerializer.string());
        // 设置value的序列化方式
        template.setValueSerializer(new JdkSerializationRedisSerializer());
        // 设置hash的key的序列化方式
        template.setHashKeySerializer(new JdkSerializationRedisSerializer());
        // 设置hash的value的序列化方式
        template.setHashValueSerializer(RedisSerializer.string());

        template.afterPropertiesSet();
        return template;
    }
}
