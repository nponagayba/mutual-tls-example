package com.example.mutualtlsexample.server;

import static org.springframework.security.config.Customizer.withDefaults;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class ServerConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(registry ->
                        registry
                                .requestMatchers("/api/tls-only")
                                .hasRole("TRUSTED")
                                .requestMatchers("/api/test")
                                .permitAll()
                                .anyRequest()
                                .denyAll()
                )
                .x509(withDefaults())
                .build();
    }
}