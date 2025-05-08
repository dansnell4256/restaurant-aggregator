package com.catalogue.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;


/**
 * Security configuration to allow access to H2 console and test endpoints
 * This is only active in development environments (local, dev, test profiles)
 */
@Configuration
@EnableWebSecurity
@Order(99) // Make this configuration have lower precedence than any other security configs
@Profile({"local", "dev", "test"})
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // For H2 Console
        http.csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));

        http.authorizeHttpRequests(auth -> auth
                // Allow H2 Console
                .requestMatchers("/h2-console/**").permitAll()
                // Allow diagnostics endpoints
                .requestMatchers("/api/diagnostics/**").permitAll()
                .requestMatchers("/api/test/**").permitAll()
                // Allow actuator endpoints
                .requestMatchers("/actuator/**").permitAll()
                // Public API paths
                .requestMatchers("/api/v1/tenants/*/catalogue/**").permitAll()
                // Permit all for development purposes - REMOVE IN PRODUCTION
                .anyRequest().permitAll()
        );

        return http.build();
    }
}
