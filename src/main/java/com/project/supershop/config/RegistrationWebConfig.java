package com.project.supershop.config;

import com.google.api.client.util.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class RegistrationWebConfig {
    @Value("${spring.security.oauth2.client.registration.google.introspection-uri}")
    private String intrspectionUri;

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public OpaqueTokenIntrospector introspector(WebClient.Builder webClientBuilder) {
        return new GoogleOpaqueTokenIntrospector(webClientBuilder);
    }


    @Bean
    public WebClient userClientInfo(WebClient.Builder webClientBuilder) {
        return webClientBuilder.baseUrl("https://www.googleapis.com").build();
    }
}
