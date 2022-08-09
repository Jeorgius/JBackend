package com.jeorgius.jbackend.config;

import com.jeorgius.jbackend.http.MultipartFileReader;
import com.jeorgius.jbackend.http.PageableFeignEncoder;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.apache.tika.Tika;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Profile("!unittesting")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.jeorgius.jbackend"})
public class CommonConfig {
    @Autowired
    private ObjectFactory<HttpMessageConverters> messageConverters;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Primary
    @Bean
    public Encoder feignFormEncoder() {
        return new PageableFeignEncoder(new SpringFormEncoder(new SpringEncoder(messageConverters)));
    }

    @Primary
    @Bean
    public Decoder decoder() {
        List<HttpMessageConverter<?>> springConverters =
                messageConverters.getObject().getConverters();

        List<HttpMessageConverter<?>> decoderConverters =
                new ArrayList<>(springConverters.size() + 1);

        decoderConverters.addAll(springConverters);
        decoderConverters.add(new MultipartFileReader());

        HttpMessageConverters httpMessageConverters = new HttpMessageConverters(decoderConverters);

        return new ResponseEntityDecoder(new SpringDecoder(() -> httpMessageConverters));
    }

    @Primary
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public Tika getTika() {
        return new Tika();
    }
}
