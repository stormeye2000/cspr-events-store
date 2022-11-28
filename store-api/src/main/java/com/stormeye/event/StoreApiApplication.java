package com.stormeye.event;

import static java.util.Collections.singletonList;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * SpringBoot Application Entry Point for the Casper Events API.
 */
@SpringBootApplication
public class StoreApiApplication {
	public static void main(String[] args) {
		SpringApplication.run(StoreApiApplication.class, args);
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
