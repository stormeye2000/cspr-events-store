package com.stormeye.event.store.client;

import com.casper.sdk.service.CasperService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URI;

/**
 * Factory Bean for the CasperService
 *
 * @author ian@meywood.com
 */
@Service
public class CasperServiceFactory implements FactoryBean<CasperService> {

    /** The URI of the casper not to connect to */
    @Value("${NODE_URI:http://localhost:9999}")
    private URI nodeUri;
    /** The casper service API */
    private CasperService casperService;

    @Override
    public CasperService getObject() throws BeansException {
        if (casperService == null) {
            try {
                casperService = CasperService.usingPeer(nodeUri.getHost(), nodeUri.getPort());
            } catch (MalformedURLException e) {
                throw new BeanCreationException("Error creating casper service", e);

            }
        }
        return casperService;
    }

    @Override
    public Class<?> getObjectType() {
        return CasperService.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public URI getNodeUri() {
        return nodeUri;
    }
}
