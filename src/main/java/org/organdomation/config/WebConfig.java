package org.organdomation.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web configuration for the LifeLink application.
 * Handles CORS configuration and other web-related settings.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Configure CORS to allow requests from frontend
     * 
     * @param registry CorsRegistry to configure
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/lifelink/api/**")
                .allowedOriginPatterns("http://localhost:3000", "http://0.0.0.0:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("Content-Type", "Authorization", "X-Requested-With")
                .exposedHeaders("Authorization")
                .allowCredentials(true)
                .maxAge(3600); // 1 hour
    }
}