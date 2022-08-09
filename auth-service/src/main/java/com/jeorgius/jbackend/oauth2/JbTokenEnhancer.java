package com.jeorgius.jbackend.oauth2;

import com.jeorgius.jbackend.dto.UserDetailsDto;
import com.jeorgius.jbackend.dto.UserDto;
import com.jeorgius.jbackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JbTokenEnhancer extends JwtAccessTokenConverter {
    private static final String RESOURCE_OWNER_GRANT_TYPE = "password";
    private static final String REFRESH_TOKEN_GRANT_TYPE = "refresh_token";

    @Autowired
    private UserService userService;
    @Autowired
    public JbTokenEnhancer(@Value("${jbackend.security.jwt.signing.key}") String signingKey) {
        setVerifierKey(signingKey);
        setSigningKey(signingKey);
    }

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken(accessToken);
        if (this.isAuthenticatedAndPasswordOrRefreshGrantType(authentication)) {
            UserDetailsDto userDto = ((UserDetailsDto) authentication.getUserAuthentication().getPrincipal());
            UserDto userDTO = userService.findById(userDto.getId()).orElse(null);
            log.info(String.format("Save super user parameter (value = %d) for user %d", userDto.getSuperUserId(), userDTO == null ? 0 : userDTO.getId()));
//            token.setAdditionalInformation(commonUtils.getAdditionalParameters(userDto.getSuperUserId(), userDTO));
        }
        return super.enhance(token, authentication);

    }

    private boolean isAuthenticatedAndPasswordOrRefreshGrantType(OAuth2Authentication authentication) {
        return isPasswordOrRefreshGrantType(authentication) &&
                authentication.isAuthenticated() &&
                UserDetailsDto.class.isAssignableFrom(authentication.getUserAuthentication().getPrincipal().getClass());
    }

    private boolean isPasswordOrRefreshGrantType(OAuth2Authentication authentication) {
        return RESOURCE_OWNER_GRANT_TYPE.equals(authentication.getOAuth2Request().getGrantType())
                || (authentication.getOAuth2Request().getRefreshTokenRequest() != null &&
                REFRESH_TOKEN_GRANT_TYPE.equals(authentication.getOAuth2Request().getRefreshTokenRequest().getGrantType()));
    }
}
