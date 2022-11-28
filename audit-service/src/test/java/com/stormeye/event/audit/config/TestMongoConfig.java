package com.stormeye.event.audit.config;

import com.stormeye.mongo.AbstractMongoConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Mongo database spring configuration
 *
 * @author ian@meywood.com
 */
@Configuration
@EnableMongoRepositories(basePackages = "com.stormeye.event.audit")
public class TestMongoConfig extends AbstractMongoConfig {

    /** The name of the database to connect to */
    protected TestMongoConfig(@Value("${spring.data.mongodb.uri:mongodb://localhost:27017/casper-events}") final String uri) {
        super(uri);
    }
}