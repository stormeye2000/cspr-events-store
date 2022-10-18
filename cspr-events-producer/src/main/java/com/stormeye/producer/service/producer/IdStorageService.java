package com.stormeye.producer.service.producer;

import com.casper.sdk.model.event.EventType;
import com.mongodb.BasicDBObject;
import com.mongodb.client.model.Indexes;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.net.URI;

/**
 * The service for saving and obtaining the current event IDs
 *
 * @author ian@meywood.com
 */
@Service
public class IdStorageService {

    private static final String EVENT_IDS = "eventIds";
    public static final String SOURCE = "source";
    public static final String TYPE = "type";
    public static final String ID = "id";
    private final MongoOperations mongoOperations;

    public IdStorageService(@Autowired final MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
        createIndex();
    }

    /**
     * Obtains the ID of the last processed event of the specified event type
     *
     * @param source    the host/node that is the source of the event
     * @param eventType the type of event to obtain the ID for
     * @return the last ID if present otherwise 0
     */
    public long getCurrentId(final URI source, final EventType eventType) {
        var eventId = mongoOperations.findOne(createQuery(source, eventType), Document.class, EVENT_IDS);
        return eventId != null ? eventId.getLong(ID) : 0L;
    }

    /**
     * Obtains next ID of the last processed event of the specified event type, which is the last ID + 1
     *
     * @param source    the host/node that is the source of the event
     * @param eventType the type of event to obtain the ID for
     * @return the next ID if present otherwise 0
     */
    public long getNextId(final URI source, final EventType eventType) {
        long currentId = getCurrentId(source, eventType);
        // If the ID is zero use it otherwise increment it
        return currentId == 0 ? currentId : currentId + 1;
    }

    /**
     * Set the ID of the last processed event of the specified event type
     *
     * @param source    the host/node that is the source of the event
     * @param eventType the type of event to obtain the ID for
     * @param id        the last ID to persist
     */
    public void setCurrentEvent(final URI source, final EventType eventType, final long id) {

        mongoOperations.upsert(
                createQuery(source, eventType),
                Update.update(SOURCE, source).set(TYPE, eventType.name()).set(ID, id),
                EVENT_IDS
        );
    }

    @NotNull
    private Query createQuery(final URI source, final EventType eventType) {
        return Query.query(Criteria.where(SOURCE).is(source.toString()).and(TYPE).is(eventType.name()));
    }

    private void createIndex() {
        this.mongoOperations.getCollection(EVENT_IDS).createIndex(
                Indexes.compoundIndex(new BasicDBObject(SOURCE, 1), new BasicDBObject(TYPE, 1))
        );
    }
}
