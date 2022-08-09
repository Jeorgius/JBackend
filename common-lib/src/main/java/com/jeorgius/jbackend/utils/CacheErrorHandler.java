package com.jeorgius.jbackend.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.data.redis.RedisConnectionFailureException;

@Slf4j
public class CacheErrorHandler implements org.springframework.cache.interceptor.CacheErrorHandler {

    @Override
    public void handleCacheGetError(RuntimeException exception,
                                    Cache cache,
                                    Object key) {
        handle(exception, cache, key);
    }

    @Override
    public void handleCachePutError(RuntimeException exception,
                                    Cache cache,
                                    Object key,
                                    Object value) {
        handle(exception, cache, key);
    }

    @Override
    public void handleCacheEvictError(RuntimeException exception,
                                      Cache cache,
                                      Object key) {
        handle(exception, cache, key);
    }

    @Override
    public void handleCacheClearError(RuntimeException exception,
                                      Cache cache) {
        handle(exception, cache, null);
    }

    private void handle(RuntimeException exception,
                        Cache cache,
                        Object key) {
        if(exception instanceof RedisConnectionFailureException) {
            if(key == null) {
                log.warn("Redis connection failure. Cache: {}. Falling back to service invocation", cache.getName());
            } else {
                log.warn("Redis connection failure. Cache: {}, key: {}. Falling back to service invocation", cache.getName(), key);
            }
            return;
        }

        throw exception;
    }
}
