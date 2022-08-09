package com.jeorgius.jbackend.oauth2;

import com.jeorgius.jbackend.dto.UserDetailsDto;
import com.jeorgius.jbackend.service.UserService;
import com.jeorgius.jbackend.utils.BaseUtils;
import com.jeorgius.jbackend.utils.UserUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.util.Map;

import static com.jeorgius.jbackend.utils.UserUtils.hideEmail;

@Slf4j
@AllArgsConstructor
public class JbAuthenticationProvider extends DaoAuthenticationProvider {
    private UserService userService;
    private UserUtils userUtils;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.info(String.format("Trying to login as %s", hideEmail(authentication.getName())));
        Authentication currentUserAuthentication = super.authenticate(authentication);
        if (currentUserAuthentication.isAuthenticated() && authentication.getDetails() instanceof Map && currentUserAuthentication.getPrincipal() instanceof UserDetailsDto) {
            UserDetailsDto currentUser = (UserDetailsDto) currentUserAuthentication.getPrincipal();

            log.info(String.format("Logged in as %s (id: %d)", hideEmail(currentUser.getUsername()), currentUser.getId()));
            long asUserId = BaseUtils.getLong((String) ((Map) authentication.getDetails()).get(UserUtils.AS_USER_PARAM_NAME), 0);
            if (asUserId > 0) {
                //ToDo use real users from DB
                UserDetailsDto otherUser = userService.findUserDetails(asUserId);

                log.info(String.format("Login as %s (id: %d) from superuser %s (id: %d)", hideEmail(otherUser.getUsername()), asUserId, hideEmail(currentUser.getUsername()), currentUser.getId()));
                otherUser.setSuperUserId(currentUser.getId());
                currentUserAuthentication = createSuccessAuthentication(otherUser, currentUserAuthentication, otherUser);
            }
        }
        return currentUserAuthentication;
    }

}
