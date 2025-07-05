package com.codebuddy.audio;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig
{

    @Value("${deepgram.api.base-url}")
    private String baseUrl;

    @Value("${deepgram.api.key}")
    private String apiKey;



    @Bean
    public WebClient webClient() {
        System.out.println("Creating WebClient Bean");
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Token " + apiKey)
                .build();
    }
}
