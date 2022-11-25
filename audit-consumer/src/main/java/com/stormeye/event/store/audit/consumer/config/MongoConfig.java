package com.stormeye.event.store.audit.consumer.config;

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
@EnableMongoRepositories(basePackages = "com.stormeye.event.store.audit")
public class MongoConfig extends AbstractMongoConfig {

    /** The name of the database to connect to */
    @Value("${spring.data.mongodb.database:casper-events}")
    private String databaseName;
    /** The mongo database host name */
    @Value("${spring.data.mongodb.host:localhost:27017}")
    private String host;

    @Override
    public String getDatabaseName() {
        return databaseName;
    }

    @Override
    protected String getHost() {
        return host;
    }
}