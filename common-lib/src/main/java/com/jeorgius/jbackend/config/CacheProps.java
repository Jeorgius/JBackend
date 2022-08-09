package com.jeorgius.jbackend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "jbackend.cache")
public class CacheProps {

    @Data
    public static class EntryTtl {
        private Duration global;
        private Map<String, Duration> override;
    }

    private EntryTtl ttl;
}
