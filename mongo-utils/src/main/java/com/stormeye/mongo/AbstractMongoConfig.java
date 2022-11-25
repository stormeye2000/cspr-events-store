package com.stormeye.mongo;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

/**
 * Abstract Mongo database spring configuration
 *
 * @author ian@meywood.com
 */

public abstract class AbstractMongoConfig extends AbstractMongoClientConfiguration {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /** The name of the database to connect to */

    @Override
    public MappingMongoConverter mappingMongoConverter(final MongoDatabaseFactory databaseFactory,
                                                       final MongoCustomConversions customConversions,
                                                       final MongoMappingContext mappingContext) {

        var converter = super.mappingMongoConverter(databaseFactory, customConversions, mappingContext);
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        return converter;
    }

    @Override
    public MongoClient mongoClient() {

        var connectionString = new ConnectionString(
                "mongodb://" + getHost()
        );

        logger.debug("Will connect to mongo [{}]", connectionString);

        return MongoClients.create(
                MongoClientSettings.builder()
                        .applyConnectionString(connectionString)
                        .build()
        );
    }

    protected abstract String getHost();
}
