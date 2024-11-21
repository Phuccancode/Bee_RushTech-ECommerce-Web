package com.project.bee_rushtech.configs;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CORSConfiguration {
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000")); // Allow all origins
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE",
                "OPTIONS")); // Allowed methods
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type",
                "Accept"));
        configuration.setAllowCredentials(true); // Allow credentials
        configuration.setMaxAge(3600L);
        // How long the response from a pre-flight request can be cached by clients
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Apply this configuration to all paths
        return source;
    }
}