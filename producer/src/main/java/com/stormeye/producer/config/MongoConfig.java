package com.stormeye.producer.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

/**
 * Mongo database spring configuration
 *
 * @author ian@meywood.com
 */
@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {

    private final Logger logger = LoggerFactory.getLogger(MongoConfig.class);
    /** The mongo database host name including port and database name eg:
     * <pre>mongodb://[username:password@]host1[:port1][,host2[:port2],...[,hostN[:portN]]][/[database.collection][?options]]</pre>
     */
    @Value("${spring.data.mongodb.host:localhost:27017/casper-producer}")
    private String host;

    @Override
    public String getDatabaseName() {
        var split = host.split("/");
        return split.length > 1 ? split[split.length - 1] : null;
    }

    @Override
    public MongoClient mongoClient() {

        var connectionString = new ConnectionString("mongodb://" + host);

        logger.debug("Will connect to mongo [{}]", connectionString);

        return MongoClients.create(
                MongoClientSettings.builder()
                        .applyConnectionString(connectionString)
                        .build()
        );
    }
}
