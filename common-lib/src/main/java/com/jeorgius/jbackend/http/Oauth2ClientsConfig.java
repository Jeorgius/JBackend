package com.jeorgius.jbackend.http;

import feign.RequestInterceptor;
import feign.codec.Decoder;
import feign.codec.Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

@RequiredArgsConstructor
public class Oauth2ClientsConfig extends Oauth2BaseConfig {
    private final Encoder encoder;
    private final Decoder decoder;

    @Bean
    public Encoder getEncoder() {
        return encoder;
    }

    @Bean
    public Decoder getDecoder() {
        return decoder;
    }

    @Bean
    public RequestInterceptor oauth2FeignRequestInterceptor() {
        return requestTemplate -> {
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                try {
                    Object details = SecurityContextHolder.getContext().getAuthentication().getDetails();
                    String token = details instanceof String ? (String) details : ((OAuth2AuthenticationDetails) details).getTokenValue();
                    requestTemplate.header("Authorization", "Bearer " + token);
                } catch (Exception ex) {
                }
            }
        };
    }
}
