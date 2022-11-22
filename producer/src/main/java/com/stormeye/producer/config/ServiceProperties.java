package com.stormeye.producer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;

/**
 * Reads the application.yml properties
 * Enables lists of emitters and topics
 */
@Component("ServiceProperties")
@Configuration
@ConfigurationProperties(prefix = "services")
@EnableConfigurationProperties
public class ServiceProperties {

    /** URIs of the casper nodes to obtain event streams, provided as a comma separated string */
    private List<URI> emitters;
    private final Environment env;

    public ServiceProperties(final Environment env) {
        this.env = env;
    }

    public List<URI> getEmitters() {
        return emitters;
    }

    public void setEmitters(final List<URI> emitters) {
        this.emitters = emitters;
    }

    public String getBootstrapServers() {
        return env.getProperty("KAFKA_SERVICE_SERVICE_HOST") + ":" + env.getProperty("KAFKA_SERVICE_SERVICE_PORT");
    }
}
