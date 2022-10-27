package com.stormeye.event;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * Tne spring boot application entry point
 *
 * @author ian@meywood.com
 */
@SpringBootApplication
@EnableWebMvc
public class AuditApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuditApiApplication.class, args);
    }
}
