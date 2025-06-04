package com.example.interviewsimulator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login.html", "/oauth2/**", "/css/**", "/js/**", "/assets/**").permitAll()
                .requestMatchers("/api/**").authenticated()
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth -> oauth
                .defaultSuccessUrl("https://atharvpandey13-2006.github.io/AtharvPandey13-2006.github.io-interview/", true)  // redirect to frontend after login
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login.html")
                .permitAll()
            );

        return http.build();
    }
}
