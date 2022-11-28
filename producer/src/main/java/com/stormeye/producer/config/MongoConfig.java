package com.stormeye.producer.config;

import com.stormeye.mongo.AbstractMongoConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Mongo database spring configuration
 *
 * @author ian@meywood.com
 */
@Configuration
public class MongoConfig extends AbstractMongoConfig {

    public MongoConfig(@Value("${spring.data.mongodb.uri:mongodb://localhost:27017/casper-producer}") final String uri) {
        super(uri);
    }
}
