package com.jeorgius.jbackend.config;

public interface ServiceInstanceOperation {
    <T> T execute(String serviceAddress);
}
