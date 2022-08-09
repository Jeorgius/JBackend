package com.jeorgius.jbackend.enums;

import org.springframework.security.core.GrantedAuthority;

//ToDo move to the dedicated library that is being used by
public enum Role implements GrantedAuthority {
    ADMIN("Admin"),
    ANALYST("Analyst"),
    TEST("Test"),
    SUPERUSER("Superuser");

    private String description;

    Role(String description) {
        this.description = description;
    }

    @Override
    public String getAuthority() {
        return name();
    }
}
