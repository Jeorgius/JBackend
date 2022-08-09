package com.jeorgius.jbackend.config;

import com.jeorgius.jbackend.oauth2.JbDefaultAuthenticationKeyGenerator;
import com.jeorgius.jbackend.oauth2.JbTokenEnhancer;
import com.jeorgius.jbackend.service.UserService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

//@ConditionalOnProperty(name = "jbackend.security.enabled", havingValue = "true")
@Configuration
@EnableAuthorizationServer
@RequiredArgsConstructor
public class OAuth2ServerConfig extends AuthorizationServerConfigurerAdapter {

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final OAuthClientConfigurationProperties properties;
    private final JbTokenEnhancer jbTokenEnhancer;
    private final RedisConnectionFactory redisConnectionFactory;

    @Value("${jbackend.security.access.token.validity.seconds:3600}")
    private int accessTokenValiditySeconds;

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient(properties.getClient())
                .authorizedGrantTypes(properties.getGrantTypes())
                .secret(passwordEncoder.encode(properties.getSecret()))
                .scopes(properties.getScopes())
                .accessTokenValiditySeconds(accessTokenValiditySeconds);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                .tokenStore(tokenStore(redisConnectionFactory))
                .authenticationManager(authenticationManager)
                .userDetailsService(userService)
                .tokenEnhancer(jbTokenEnhancer);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.checkTokenAccess("permitAll()");
    }

    @Bean
    public TokenStore tokenStore(RedisConnectionFactory redisConnectionFactory) {
        var tokenStore = new RedisTokenStore(redisConnectionFactory);
        tokenStore.setAuthenticationKeyGenerator(new JbDefaultAuthenticationKeyGenerator());
        userService.setTokenStore(tokenStore);
        return tokenStore;
    }

    @Configuration
    @Getter
    @Setter
    @ConfigurationProperties(prefix = "jbackend.security")
    public static class OAuthClientConfigurationProperties {
        private String client;
        private String secret;
        private String[] grantTypes;
        private String[] scopes;
    }
}
