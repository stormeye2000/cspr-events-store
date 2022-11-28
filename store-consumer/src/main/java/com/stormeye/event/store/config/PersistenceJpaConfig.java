package com.stormeye.event.store.config;

import com.stormeye.event.config.AbstractPersistenceJpaConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Factory Bean for JPA configuration
 *
 * @author ian@meywood.com
 */
@Configuration
@EnableTransactionManagement
public class PersistenceJpaConfig extends AbstractPersistenceJpaConfig {

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;
    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password:}")
    private String password;
    @Value("${hibernate.hbm2ddl.auto:update}")
    private String hbm2ddlAuto;
    @Value("${hibernate.dialect}")
    private String dialect;

    @Override
    protected String getDriverClassName() {
        return driverClassName;
    }

    @Override
    protected String getDialect() {
        return dialect;
    }

    @Override
    protected String getHbm2ddlAuto() {
        return hbm2ddlAuto;
    }

    @Override
    protected String getPassword() {
        return password;
    }

    @Override
    protected String getUrl() {
        return url;
    }

    @Override
    protected String getUsername() {
        return username;
    }
}
