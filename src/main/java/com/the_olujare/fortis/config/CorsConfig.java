package com.the_olujare.fortis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configures Cross-Origin Resource Sharing (CORS) for the application.
 * Allows the frontend to communicate with the backend across different origins.
 *
 * Applies CORS rules only to /api/** endpoints.
 * This prevents exposing unnecessary routes.
 *
 * allowedOrigins
 *  - Explicitly allows requests from the local frontend "(http://localhost:3000)."
 *  - Blocks all other origins by default.
 *
 * allowedMethods
 *  - Permits common REST operations (GET, POST, PUT, DELETE, PATCH).
 *  - Includes OPTIONS to support browser preflight requests.
 *
 * allowedHeaders
 *  - Accepts all request headers, including Authorization.
 *
 * allowCredentials
 *  - Enables cookies or authorization headers (JWT) to be sent with requests.
 *
 * This setup is safe for development.
 * Production should restrict origins and headers more aggressively.
 */

@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry corsRegistry) {
                corsRegistry.addMapping("/api/**")
                        .allowedOrigins("http://localhost:5173")
                        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}