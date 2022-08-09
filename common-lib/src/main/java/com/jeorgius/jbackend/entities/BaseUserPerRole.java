package com.jeorgius.jbackend.entities;

import com.jeorgius.jbackend.enums.Role;

public interface BaseUserPerRole {
    Long getId();

    Role getRole();

    boolean isSuperuser();
}
