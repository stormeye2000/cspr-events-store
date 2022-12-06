package com.stormeye.event.api.config;

import static java.util.Collections.singletonList;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.DispatcherServlet;

import java.util.List;
import javax.annotation.PostConstruct;

/**
 * @author ian@meywood.com
 */
@Component
public class WebConfig {

    private final DispatcherServlet dispatcherServlet;

    public WebConfig(final DispatcherServlet dispatcherServlet) {
        this.dispatcherServlet = dispatcherServlet;
    }

    @PostConstruct
    public void configureDispatcherServlet() {
        this.dispatcherServlet.setThrowExceptionIfNoHandlerFound(true);
    }

    @Bean
    @ConditionalOnProperty(value = "management.endpoints.web.cors.allowed-origins")
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${management.endpoints.web.cors.allowed-origins}") final List<String> corsOrigins) {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.setAllowedOrigins(corsOrigins);
        configuration.setAllowedMethods(singletonList("GET"));
        configuration.setAllowedHeaders(singletonList("*"));
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
