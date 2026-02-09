package com.nhnacademy.shop.common.config;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;


@EnableRedisRepositories
@Configuration
@RequiredArgsConstructor
public class RedisConfig {
    @Value("${spring.data.redis.host}")
    private String REDIS_HOST;
    @Value("${spring.data.redis.port}")
    private int REDIS_PORT;
    @Value("${spring.data.redis.password}")
    private String REDIS_PASSWORD;

    private int orderDb = 0;
    private int carDb = 1;
    private int authDb = 2;

    @Bean(name = "orderRedisConnectionFactory")
    public RedisConnectionFactory orderRedisConnectionFactory() {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        redisConfig.setHostName(REDIS_HOST);
        redisConfig.setPort(REDIS_PORT);
        redisConfig.setPassword(REDIS_PASSWORD);
        redisConfig.setDatabase(orderDb);

        return new LettuceConnectionFactory(redisConfig);
    }

    @Bean(name = "cartRedisConnectionFactory")
    public RedisConnectionFactory cartRedisConnectionFactory() {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        redisConfig.setHostName(REDIS_HOST);
        redisConfig.setPort(REDIS_PORT);
        redisConfig.setPassword(REDIS_PASSWORD);
        redisConfig.setDatabase(carDb);

        return new LettuceConnectionFactory(redisConfig);
    }

    @Bean(name = "authRedisFactory")
    public RedisConnectionFactory authRedisFactory() {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        redisConfig.setHostName(REDIS_HOST);
        redisConfig.setPort(REDIS_PORT);
        redisConfig.setPassword(REDIS_PASSWORD);
        redisConfig.setDatabase(authDb);

        return new LettuceConnectionFactory(redisConfig);
    }

    @Bean(name = "orderRedisTemplate")
    public RedisTemplate<String, Object> orderRedisTemplate(RedisConnectionFactory orderRedisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(orderRedisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setEnableTransactionSupport(true);
        return redisTemplate;
    }

    @Bean(name = "cartRedisTemplate")
    public RedisTemplate<String, Object> cartRedisTemplate(RedisConnectionFactory cartRedisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(cartRedisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        return redisTemplate;
    }

    @Bean(name = "authRedisTemplate")
    public RedisTemplate<String, String> authRedisTemplate(RedisConnectionFactory authRedisFactory) {
        RedisTemplate<String, String> authRedisTemplate = new RedisTemplate<>();
        authRedisTemplate.setConnectionFactory(authRedisFactory);
        authRedisTemplate.setKeySerializer(new StringRedisSerializer());
        authRedisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        authRedisTemplate.setHashKeySerializer(new StringRedisSerializer());
        authRedisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        authRedisTemplate.afterPropertiesSet();
        return authRedisTemplate;
    }

    @Primary
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory orderRedisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(orderRedisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}
