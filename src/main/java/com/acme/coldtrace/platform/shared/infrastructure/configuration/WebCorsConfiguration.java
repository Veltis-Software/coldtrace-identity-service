package com.acme.coldtrace.platform.shared.infrastructure.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebCorsConfiguration implements WebMvcConfigurer {
    private final List<String> allowedOriginPatterns;
    private final List<String> allowedMethods;
    private final List<String> allowedHeaders;
    private final List<String> exposedHeaders;
    private final long maxAge;

    public WebCorsConfiguration(
            @Value("${cors.allowed-origin-patterns}") List<String> allowedOriginPatterns,
            @Value("${cors.allowed-methods}") List<String> allowedMethods,
            @Value("${cors.allowed-headers}") List<String> allowedHeaders,
            @Value("${cors.exposed-headers}") List<String> exposedHeaders,
            @Value("${cors.max-age}") long maxAge
    ) {
        this.allowedOriginPatterns = allowedOriginPatterns;
        this.allowedMethods = allowedMethods;
        this.allowedHeaders = allowedHeaders;
        this.exposedHeaders = exposedHeaders;
        this.maxAge = maxAge;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(allowedOriginPatterns.toArray(String[]::new))
                .allowedMethods(allowedMethods.toArray(String[]::new))
                .allowedHeaders(allowedHeaders.toArray(String[]::new))
                .exposedHeaders(exposedHeaders.toArray(String[]::new))
                .maxAge(maxAge);
    }
}
