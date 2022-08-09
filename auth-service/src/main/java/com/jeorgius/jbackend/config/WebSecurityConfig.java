package com.jeorgius.jbackend.config;

import com.jeorgius.jbackend.oauth2.JbAuthenticationProvider;
import com.jeorgius.jbackend.service.UserService;
import com.jeorgius.jbackend.utils.UserUtils;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserService userService;
    private final UserUtils userUtils;
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public JbAuthenticationProvider fdppcaAuthenticationProvider() {
        JbAuthenticationProvider provider = new JbAuthenticationProvider(userService, userUtils);
        provider.setUserDetailsService(userService);
        return provider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(fdppcaAuthenticationProvider());
    }
}
