package com.jeorgius.jbackend.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Slf4j
@ConditionalOnProperty(name = "jbackend.security.enabled", havingValue = "true")
@Configuration
@EnableFeignClients
@EnableResourceServer
@EnableWebSecurity
public class GatewayConfig extends ResourceServerConfigurerAdapter {

    private static final String[] AUTH_WHITELIST = {
            "/**/v2/api-docs",
            "/**/swagger-resources",
            "/**/swagger-resources/**",
            "/**/configuration/ui",
            "/**/configuration/security",
            "/**/swagger-ui.html",
            "/**/webjars/**",
            "/inbox-service/**",
            "/user-service/users/hash/**",
            "/user-service/users/set-password",
    };

    @Bean
    public RestOperations getRestOperations() {
        var restTemplate = new RestTemplate();

        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                if (response.getRawStatusCode() != 400) {
                    super.handleError(response);
                }
            }
        });
        return restTemplate;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/actuator/**").permitAll()
                .antMatchers("/user-service/oauth/token").permitAll()
                .antMatchers(AUTH_WHITELIST).permitAll()
                .anyRequest().authenticated()
                .and().csrf().disable();
    }
}
