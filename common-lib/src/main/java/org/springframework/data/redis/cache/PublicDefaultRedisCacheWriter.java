package org.springframework.data.redis.cache;

import org.springframework.data.redis.connection.RedisConnectionFactory;

// DefaultRedisCacheWriter has package-private access, so extend it here
public class PublicDefaultRedisCacheWriter extends DefaultRedisCacheWriter {
    public PublicDefaultRedisCacheWriter(RedisConnectionFactory connectionFactory) {
        super(connectionFactory, new BatchStrategies.Keys());
    }
}
