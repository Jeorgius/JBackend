package com.jeorgius.jbackend.enums;

public enum UserStatus {
    NEW("New"),
    CONFIRMED("Confirmed"),
    DELETED("Deleted");

    private final String description;

    UserStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
