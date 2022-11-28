package com.stormeye.mongo;

import com.mongodb.BasicDBObject;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;

/**
 * Utility methods for mongo database
 *
 * @author ian@meywood.com
 */
public class MongoUtils {

    private MongoUtils() {
        // prevent construction
    }

    /**
     * Deletes all objects from all collections.
     *
     * @param mongoOperations the Spring Data Mongo APO
     */
    public static void deleteAllDocuments(final MongoOperations mongoOperations) {

        mongoOperations.getCollectionNames().forEach(collection ->
                mongoOperations.getCollection(collection).deleteMany(new BasicDBObject())
        );
    }

    /**
     * Deletes all files from all GridFS.
     *
     * @param gridFsOperations the Spring Data GridFS API
     */
    public static void deleteAllFiles(final GridFsOperations gridFsOperations) {
        gridFsOperations.delete(new Query());
    }
}
