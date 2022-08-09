package com.jeorgius.jbackend.http.client;

import com.jeorgius.jbackend.http.Oauth2ClientsConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;


@Component
@FeignClient(name = "auth-service", configuration = Oauth2ClientsConfig.class)
public interface UserClient {
}
