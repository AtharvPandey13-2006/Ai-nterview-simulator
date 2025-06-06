package com.example.interviewsimulator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

import org.springframework.boot.web.servlet.server.CookieSameSiteSupplier;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import jakarta.servlet.SessionCookieConfig;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors()
            .and()
            .csrf().disable()
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login.html", "/oauth2/**", "/css/**", "/js/**", "/assets/**", "/redirect-after-login").permitAll()
                .requestMatchers("/api/**").authenticated()
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth -> oauth
                .defaultSuccessUrl("/api/interview/redirect-after-login", true)
                .failureUrl("/login.html")
            )
            .logout(logout -> logout
                .logoutSuccessUrl("https://atharvpandey13-2006.github.io")
                .permitAll()
            )
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(401);
                    response.setHeader("Access-Control-Allow-Origin", "https://atharvpandey13-2006.github.io");
                    response.setHeader("Access-Control-Allow-Credentials", "true");
                    response.getWriter().write("Unauthorized");
                })
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("https://atharvpandey13-2006.github.io"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public CookieSameSiteSupplier cookieSameSiteSupplier() {
        return CookieSameSiteSupplier.ofNone(); // For cross-origin session
    }

    @Bean
    public ServletContextInitializer secureCookieInitializer() {
        return servletContext -> {
            SessionCookieConfig sessionCookieConfig = servletContext.getSessionCookieConfig();
            sessionCookieConfig.setSecure(true); // Send only over HTTPS
        };
    }
}
