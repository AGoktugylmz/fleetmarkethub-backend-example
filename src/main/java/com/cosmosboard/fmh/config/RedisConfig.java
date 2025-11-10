package com.cosmosboard.fmh.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisKeyValueAdapter;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import java.time.Duration;
import java.util.Map;

@Slf4j
@Configuration
@EnableCaching
@EnableRedisRepositories(enableKeyspaceEvents = RedisKeyValueAdapter.EnableKeyspaceEvents.ON_STARTUP)
class RedisConfig {
    @Getter
    private static JedisPool jedisPool;

    private static final int CACHE_TTL_MINUTES = 10;

    @Value("${app.redis.host}") private String host;

    @Value("${app.redis.port}") private Integer port;

    @Value("${app.redis.password}") private String password;

    @Value("${app.redis.database}") private Integer database;

    @Value("${app.redis.timeout}") private Integer timeout;


    @Bean
    public JedisPool jedisPool() {
        log.debug("RedisConfig: host={}, port={}, password={}, timeout={}", host, port, password, timeout);
        return new JedisPool(new JedisPoolConfig(), host, port, timeout, password);
    }

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        final RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        if (database != null)
            redisStandaloneConfiguration.setDatabase(database);
        redisStandaloneConfiguration.setHostName(host);
        if (password != null)
            redisStandaloneConfiguration.setPassword(password);
        redisStandaloneConfiguration.setPort(port);
        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }

    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(CACHE_TTL_MINUTES))
                .disableCachingNullValues()
                .serializeValuesWith(
                    RedisSerializationContext.SerializationPair.fromSerializer(
                        new GenericJackson2JsonRedisSerializer()
                    )
                );
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return new RedisCacheManagerBuilderCustomizer() {
            @Override
            public void customize(RedisCacheManager.RedisCacheManagerBuilder builder) {
                final Map<String, RedisCacheConfiguration> configurationMap = Map.of("category",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(30)));
                builder.withInitialCacheConfigurations(configurationMap);
            }
        };
    }
}