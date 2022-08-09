package com.jeorgius.jbackend.config;

import com.jeorgius.jbackend.utils.CollectionCache;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.PublicDefaultRedisCacheWriter;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Configuration
@EnableCaching
@Profile("!unittesting")
@RequiredArgsConstructor
public class CacheConfig
        extends CachingConfigurerSupport {

    public static final String RESPONSE_ENTITY_CACHE_PREFIX = "RE";
    public static final String ESIA_REDIS_KEY_PREFIX = "ESIA_";

    public static final String USER_IDS_CACHE_NAME = RESPONSE_ENTITY_CACHE_PREFIX + "userIds";
    public static final String USER_SHORT_IDS_CACHE_NAME = RESPONSE_ENTITY_CACHE_PREFIX + "userShortIds";

    private final CacheProps cacheProps;

    @Bean
    public RedisCacheConfiguration defaultConfiguration() {
        return build(cacheProps.getTtl().getGlobal());
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory,
                                     RedisCacheConfiguration defaultConfig) {
        var writer = new PublicDefaultRedisCacheWriter(connectionFactory);
        var initialCaches = cacheProps.getTtl().getOverride()
                               .entrySet()
                               .stream()
                               .collect(toMap(Map.Entry::getKey,
                                              e -> build(e.getValue())));

        return new RedisCacheManager(writer, defaultConfig, initialCaches) {
            @Override
            protected Cache decorateCache(Cache cache) {
                return new CollectionCache(cache);
            }
        };
    }

    @Override
    public CacheErrorHandler errorHandler() {
        return new com.jeorgius.jbackend.utils.CacheErrorHandler();
    }

    private static RedisCacheConfiguration build(Duration ttl) {
        return RedisCacheConfiguration.defaultCacheConfig()
                                      .disableCachingNullValues()
                                      .entryTtl(ttl);
    }
}
