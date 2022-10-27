package com.stormeye.event.api.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.DispatcherServlet;

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
}
