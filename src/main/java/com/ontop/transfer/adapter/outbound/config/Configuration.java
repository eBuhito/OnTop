package com.ontop.transfer.adapter.outbound.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@org.springframework.context.annotation.Configuration
public class Configuration {

    @Value("${rest-client.timeout.read:15}")
    private long readTimeout = 15L;

    @Value("${rest-client.timeout.connection:15}")
    private long connectionTimeout = 10L;

//    @Bean
//    public ObjectMapper jacksonObjectMapper() {
//        return new ObjectMapper().setPropertyNamingStrategy(new PropertyNamingStrategy.SnakeCaseStrategy());
//    }

    @Bean
    public RestTemplate defaultRestTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(connectionTimeout))
                .setReadTimeout(Duration.ofSeconds(readTimeout))
                .build();
    }


}
