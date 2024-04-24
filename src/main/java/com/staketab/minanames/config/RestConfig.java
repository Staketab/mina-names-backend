package com.staketab.minanames.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestConfig {

    @Value("${zk-cloud-worker.url}")
    private String zkCloudWorkerUrl;

    @Bean
    public RestTemplate zkCloudWorkerRestTemplate(final RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.rootUri(zkCloudWorkerUrl)
                .build();
    }
}
