package com.jeorgius.jbackend.http;

import feign.Contract;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;

public abstract class Oauth2BaseConfig {

    @Bean
    public Contract getContract() {
        return new SpringMvcContract();
    }
}