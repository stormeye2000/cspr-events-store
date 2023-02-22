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

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Abstract Mongo database spring configuration
 *
 * @author ian@meywood.com
 */
public abstract class AbstractMongoConfig extends AbstractMongoClientConfiguration {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    /** The hostname of the mongo primary */
    private final String hostName;
    /** The name of the database to connect to */
    private final String databaseName;
    /**
     * The mongo database host name(s) including port and database name eg:
     * <pre>mongodb://[username:password@]host1[:port1][,host2[:port2],...[,hostN[:portN]]][/[database.collection][?options]]</pre>
     */
    private final String connectionString;

    protected AbstractMongoConfig(final String connectionString) {
        this.connectionString = connectionString;
        try {

            final String[] hosts = connectionString.split(",");
            // The URI of the expected mongo primary
            final URI uri = new URI(hosts[0]);
            this.hostName = uri.getHost();

            final String path = uri.getPath();
            if (path == null || path.length() < 2) {
                throw new IllegalArgumentException("Missing database name in uri: " + connectionString);
            }
            this.databaseName = path.substring(1);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid connectionString:" + connectionString, e);
        }
    }


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

        var connectionString = new ConnectionString(this.connectionString);

        logger.debug("Will connect to mongo [{}]", connectionString);

        return MongoClients.create(
                MongoClientSettings.builder()
                        .applyConnectionString(connectionString)
                        .build()
        );
    }

    public String getHost() {
        return this.hostName;
    }

    @Override
    public String getDatabaseName() {
        return this.databaseName;
    }
}
