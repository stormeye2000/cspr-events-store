package com.stormeye.event;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Tne spring boot application entry point
 *
 * @author ian@meywood.com
 */
@SpringBootApplication
public class AuditConsumerApplication {
    public static void main(final String[] args) {
        SpringApplication.run(AuditConsumerApplication.class, args);
    }
}
