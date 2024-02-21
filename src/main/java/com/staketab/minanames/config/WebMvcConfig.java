package com.staketab.minanames.config;

import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.apache.http.client.HttpClient;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Bean
    public HttpClient httpClient() {
        return HttpClientBuilder.create().disableCookieManagement().build();
    }
}
