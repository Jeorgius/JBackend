//package com.jeorgius.jbackend.config;
//
//import com.google.common.cache.CacheBuilder;
//import com.google.common.cache.CacheLoader;
//import com.google.common.cache.LoadingCache;
//import com.google.common.util.concurrent.UncheckedExecutionException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.authentication.AuthenticationServiceException;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.oauth2.common.OAuth2AccessToken;
//import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
//import org.springframework.security.oauth2.provider.OAuth2Authentication;
//import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
//import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
//import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
//import org.springframework.stereotype.Component;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.client.RestOperations;
//
//import javax.annotation.PostConstruct;
//import java.nio.charset.StandardCharsets;
//import java.util.Base64;
//import java.util.Map;
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.TimeUnit;
//
//@ConditionalOnProperty(name = "fdppca.security.enabled", havingValue = "true")
//@Component
//public class OAuthRemoteTokenService implements ResourceServerTokenServices {
//
//    private final Logger log = LoggerFactory.getLogger(OAuthRemoteTokenService.class);
//
//    @Autowired
//    private ServiceOperationExecutor serviceOperationExecutor;
//    @Autowired
//    private RestOperations restOperations;
//    @Value("${jbackend.oauth-service-name}")
//    private String oauthServiceName;
//    @Value("${jbackend.security.client}")
//    private String clientId;
//    @Value("${jbackend.security.secret}")
//    private String clientSecret;
//    @Value("${jbackend.token.cache.expireAfterWriteSeconds:60}")
//    private Long expireAfterWriteSeconds;
//    @Value("${jbackend.token.cache.maximumSize:1000}")
//    private Long maximumSize;
//    private AccessTokenConverter tokenConverter = new DefaultAccessTokenConverter();
//    private LoadingCache<String, OAuth2Authentication> tokens;
//
//    @PostConstruct
//    public void init() {
//        tokens = CacheBuilder.newBuilder()
//                .maximumSize(maximumSize)
//                .expireAfterWrite(expireAfterWriteSeconds, TimeUnit.SECONDS)
//                .build(new CacheLoader<>() {
//                    public OAuth2Authentication load(String key) {
//                        return loadAuthenticationImpl(key);
//                    }
//                });
//    }
//
//    @Override
//    public OAuth2Authentication loadAuthentication(String accessToken) throws AuthenticationException, InvalidTokenException {
//        try {
//            return tokens.get(accessToken);
//        } catch (ExecutionException e) {
//            return loadAuthenticationImpl(accessToken);
//        } catch (UncheckedExecutionException e) {
//            throw new InvalidTokenException(accessToken);
//        }
//    }
//
//    public OAuth2Authentication loadAuthenticationImpl(String accessToken) throws AuthenticationException, InvalidTokenException {
//        var map = serviceOperationExecutor.<Map<String, Object>>executeForService(oauthServiceName, new ServiceInstanceOperation() {
//            @SuppressWarnings("unchecked")
//            @Override
//            public Map<String, Object> execute(String serviceAddress) {
//                MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
//                formData.add("token", accessToken);
//                HttpHeaders headers = new HttpHeaders();
//                headers.set("Authorization", getAuthorizationHeader());
//
//                return restOperations.exchange(String.format("%s/oauth/check_token", serviceAddress),
//                        HttpMethod.POST, new HttpEntity<>(formData, headers), Map.class).getBody();
//            }
//        });
//
//        if (map == null) {
//            throw new AuthenticationServiceException("oauth service return nothing");
//        }
//
//        if (map.containsKey("error")) {
//            log.debug("check_token returned error: {}", map.get("error"));
//            throw new InvalidTokenException(accessToken);
//        }
//
//        // gh-838
//        if (map.get("active") != null && !map.get("active").toString().equalsIgnoreCase(Boolean.TRUE.toString())) {
//            log.debug("check_token returned active attribute: {}", map.get("active"));
//            throw new InvalidTokenException(accessToken);
//        }
//
//        return tokenConverter.extractAuthentication(map);
//
//    }
//
//    @Override
//    public OAuth2AccessToken readAccessToken(String accessToken) {
//        throw new UnsupportedOperationException("Not supported: read access token");
//    }
//
//    private String getAuthorizationHeader() {
//
//        if (clientId == null || clientSecret == null) {
//            log.warn("Null Client ID or Client Secret detected. Endpoint that requires authentication will reject request with 401 error.");
//        }
//
//        String creds = String.format("%s:%s", clientId, clientSecret);
//        return "Basic " + new String(Base64.getEncoder().encode(creds.getBytes(StandardCharsets.UTF_8)));
//    }
//}
