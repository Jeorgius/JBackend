package com.jeorgius.jbackend.utils;

import org.springframework.stereotype.Service;

@Service
public class UserUtils {
    public static final String USER_ID_PARAM_NAME = "userId";
    public static final String IS_SYSTEM_PARAM_NAME = "system";
    public static final String AS_USER_PARAM_NAME = "asUser";
    public static final String OPA_ID_PARAM_NAME = "opaId";
    public static final String SUPER_USER_PARAM_NAME = "superUser";
    public static final String REGIONAL_SYSTEM_ID_PARAM_NAME = "rsId";

    public static String hideEmail(String email) {
        if (email == null || email.length() < 3)
            return email;
        return email.substring(0, 2) + email.substring(2).replaceAll("\\w", "*");
    }
}
