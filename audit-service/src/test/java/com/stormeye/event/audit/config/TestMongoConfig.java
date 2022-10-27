package com.stormeye.event.audit.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Mongo database spring configuration
 *
 * @author ian@meywood.com
 */
@Configuration
@EnableMongoRepositories(basePackages = "com.stormeye.event.audit")
public class TestMongoConfig extends AbstractMongoClientConfiguration {

    private final Logger logger = LoggerFactory.getLogger(TestMongoConfig.class);
    /** The name of the database to connect to */
    @Value("${spring.data.mongodb.database:casper-events}")
    private String databaseName;
    /** The mongo database host name */
    @Value("${spring.data.mongodb.host:localhost:27017}")
    private String host;

    @Override
    public MappingMongoConverter mappingMongoConverter(final MongoDatabaseFactory databaseFactory,
                                                       final MongoCustomConversions customConversions,
                                                       final MongoMappingContext mappingContext) {

        var converter = super.mappingMongoConverter(databaseFactory, customConversions, mappingContext);
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        return converter;
    }

    @Override
    public String getDatabaseName() {
        return databaseName;
    }


    @Override
    public MongoClient mongoClient() {

        var connectionString = new ConnectionString(
                "mongodb://" + host
        );

        logger.debug("Will connect to mongo [{}]", connectionString);

        return MongoClients.create(
                MongoClientSettings.builder()
                        .applyConnectionString(connectionString)
                        .build()
        );
    }
}