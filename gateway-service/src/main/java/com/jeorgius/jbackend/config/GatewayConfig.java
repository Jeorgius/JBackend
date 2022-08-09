package com.jeorgius.jbackend.config;

import com.jeorgius.jbackend.filter.AccessDeniedHandler;
import com.jeorgius.jbackend.filter.AuthorizationManager;
import com.jeorgius.jbackend.filter.FilterWithoutJwt;
import com.jeorgius.jbackend.filter.RestAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.cloud.gateway.discovery.DiscoveryClientRouteDefinitionLocator;
import org.springframework.cloud.gateway.discovery.DiscoveryLocatorProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Slf4j
@ConditionalOnProperty(name = "jbackend.security.enabled", havingValue = "true")
@Configuration
@EnableFeignClients
@EnableWebSecurity
@EnableWebFluxSecurity
@EnableRedisRepositories
@RequiredArgsConstructor
public class GatewayConfig extends WebSecurityConfigurerAdapter {

    private final FilterWithoutJwt filterWithoutJwt;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final AccessDeniedHandler accessDeniedHandler;
    private final AuthorizationManager authorizationManager;

    public static final String[] AUTH_WHITELIST = {
            "/auth-service/oauth/token",
            "/inbox-service/**",
            "/actuator/**",
            "/webjars/v2/api-docs",
            "/webjars/swagger-resources",
            "/webjars/swagger-resources/**",
            "/webjars/configuration/ui",
            "/webjars/configuration/security",
            "/webjars/swagger-ui.html",
//            "/webjars/**",
            "/auth-service/users/hash/**",
            "/auth-service/users/set-password",
    };

//    @Bean
//    public DiscoveryClientRouteDefinitionLocator discoveryClientRouteLocator(
//            ReactiveDiscoveryClient discoveryClient
//    ) {
//        return new DiscoveryClientRouteDefinitionLocator(
//                discoveryClient,
//                new DiscoveryLocatorProperties()
//        );
//    }

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

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http.oauth2ResourceServer().jwt()
                .jwtAuthenticationConverter(jwtAuthenticationConverter());
        http.oauth2ResourceServer().authenticationEntryPoint(restAuthenticationEntryPoint);
        http.addFilterBefore(filterWithoutJwt, SecurityWebFiltersOrder.AUTHENTICATION);
        http.authorizeExchange()
                .pathMatchers(AUTH_WHITELIST).permitAll()
                .anyExchange().access(authorizationManager)
                .and().exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler)
                .authenticationEntryPoint(restAuthenticationEntryPoint)
                .and().csrf().disable();
        return http.build();
    }

//    @Override
//    public void configure(HttpSecurity http) throws Exception {
//        http.authorizeRequests()
////                .antMatchers("/actuator/**").permitAll()
////                .antMatchers("/auth-service/oauth/token").permitAll()
//                .antMatchers(AUTH_WHITELIST).permitAll()
//                .anyRequest().authenticated()
//                .and().csrf().disable();
//    }
    @Bean
    public Converter<Jwt, ? extends Mono<? extends AbstractAuthenticationToken>> jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        Jackson2JsonRedisSerializer<?> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

}
