package com.jeorgius.jbackend.oauth2;

import com.jeorgius.jbackend.dto.UserDetailsDto;
import com.jeorgius.jbackend.utils.UserUtils;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeSet;

public class JbDefaultAuthenticationKeyGenerator extends DefaultAuthenticationKeyGenerator {
    private static final String CLIENT_ID = "client_id";
    private static final String SCOPE = "scope";
    private static final String USERNAME = "username";

    @Override
    public String extractKey(OAuth2Authentication authentication) {
        Map<String, String> values = new LinkedHashMap<>();
        OAuth2Request authorizationRequest = authentication.getOAuth2Request();
        if (!authentication.isClientOnly()) {
            values.put(USERNAME, authentication.getName());
        }
        values.put(CLIENT_ID, authorizationRequest.getClientId());
        if (authorizationRequest.getScope() != null) {
            values.put(SCOPE, OAuth2Utils.formatParameterList(new TreeSet<>(authorizationRequest.getScope())));
        }
        if (authentication.getUserAuthentication().getDetails() instanceof Map
                && ((Map) authentication.getUserAuthentication().getDetails()).get(UserUtils.AS_USER_PARAM_NAME) != null
                && authentication.getUserAuthentication().getPrincipal() instanceof UserDetailsDto) {
            values.put(UserUtils.SUPER_USER_PARAM_NAME, String.valueOf(((UserDetailsDto) authentication.getUserAuthentication().getPrincipal()).getSuperUserId()));
        }
        return generateKey(values);
    }
}
