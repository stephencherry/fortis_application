package com.the_olujare.fortis.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Central Spring Security configuration for the application.
 *
 * Implements stateless, JWT-based authentication.
 * The server does not store authentication state between requests.
 *
 * Core decisions:
 * - CSRF is disabled because the API is token-based and does not use cookies.
 * - SessionCreationPolicy.STATELESS ensures:
 *     • No HttpSession is created
 *     • No session cookies are issued
 *     • Authentication exists only for the current request
 *
 * Authorization rules:
 * - /api/auth/** is public (registration, login, verification, password reset).
 * - H2 console and static resources are explicitly allowed for development.
 * - All other endpoints require a valid JWT.
 *
 * JWT processing:
 * - JwtAuthenticationFilter runs before UsernamePasswordAuthenticationFilter.
 * - The filter extracts, validates the token, and sets the SecurityContext.
 * - Authentication is request-scoped and rebuilt on every call.
 *
 * Password handling:
 * - BCryptPasswordEncoder is used for hashing passwords securely.
 *
 * AuthenticationManager:
 * - Delegates credential validation to configured authentication providers.
 * - Used explicitly during login.
 *
 * Result:
 * - A secure, stateless API with clear authorization boundaries.
 * - No server-side sessions. No implicit trust. Every request proves identity.
 */

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(csrf -> csrf
                        .disable()  // Disable CSRF globally
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/h2-console").permitAll()
                        .requestMatchers("/webjars/**", "/css/**", "/js/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        httpSecurity.headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.disable())
        );

        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}