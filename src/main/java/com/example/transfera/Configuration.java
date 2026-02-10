package com.example.transfera;

import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@org.springframework.context.annotation.Configuration
public class Configuration {

    @Bean
    // Bean gets injected into spring container
    // will cover this in dependency injection video
    // gives access to rest template through the app
    public RestTemplate restTemplate() {

        // configure your rest template options
        return new RestTemplate();
    }
}
