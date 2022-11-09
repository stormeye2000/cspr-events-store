package com.stormeye.event.store.config;

import com.casper.sdk.service.CasperService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.MalformedURLException;
import java.net.URI;

/**
 * Factory Bean for the CasperService
 *
 * @author ian@meywood.com
 */
@Configuration
public class CasperServiceConfig {

    /** The URI of the casper not to connect to */
    @Value("${NODE_URI:http://localhost:9999}")
    private URI nodeUri;
    /** The casper service API */
    private CasperService casperService;

    @Bean
    public CasperService casperService() throws BeansException {
        if (casperService == null) {
            try {
                casperService = CasperService.usingPeer(nodeUri.getHost(), nodeUri.getPort());
            } catch (MalformedURLException e) {
                throw new BeanCreationException("Error creating casper service", e);
            }
        }
        return casperService;
    }

    public URI getNodeUri() {
        return nodeUri;
    }
}
