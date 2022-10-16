package com.ecg.sample.messages.configuration;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author Mahdi Sharifi
 * @since 10/4/22
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate getCustomRestTemplate() {
        RestTemplateBuilder builder = new RestTemplateBuilder();
        builder.setConnectTimeout(1000)
                .setReadTimeout(5000)
                .build();
        return builder.build();
    }
}
