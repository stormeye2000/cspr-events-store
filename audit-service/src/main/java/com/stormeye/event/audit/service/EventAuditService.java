package com.stormeye.event.audit.service;

import com.casper.sdk.model.event.DataType;
import com.casper.sdk.model.event.EventType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.stormeye.event.audit.execption.AuditServiceException;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;


/**
 * The service for storing events that from Kafka into the mongo database. The events are persisted as BSON documents
 * with the collection names matching the topic/event type name.
 *
 * @author ian@meywood.com
 */
@Service
public class EventAuditService {

    /** The spring data mongo API */
    private final MongoOperations mongoOperations;
    private final EventBlobStore eventBlobStore;

    private final ObjectMapper mapper;
    private final Logger logger = LoggerFactory.getLogger(EventAuditService.class.getName());

    public EventAuditService(final MongoOperations mongoOperations, final EventBlobStore eventBlobStore) {
        this.mongoOperations = mongoOperations;
        this.eventBlobStore = eventBlobStore;
        this.mapper = ObjectMapperFactory.createObjectMapper();
        createIndexes();
    }


    /**
     * Saves a JSON event as a BSON document in mongo. The json is stored in the collection of its event type/topic
     *
     * @param jsonEvent the JSON event read from Kafka
     * @return the BSON document that was persisted in mongo
     */
    public EventInfo save(final String jsonEvent) {

        final EventInfo eventInfo;
        final byte[] rawJson;

        try {
            eventInfo = mapper.readValue(jsonEvent, EventInfo.class);
            rawJson = eventInfo.getData().getBytes(StandardCharsets.UTF_8);
            eventInfo.setBytes(rawJson.length);
        } catch (Exception e) {
            throw new AuditServiceException("Error parsing JSON: " + jsonEvent, e);
        }

        // Obtain the event to use as the collection/topic name
        var eventType = Objects.requireNonNull(
                eventInfo.getEventType(),
                "\"type\" must be present in the JSON."
        );

        try {
            if (isNotVersionType(eventInfo)) {
                mongoOperations.save(eventInfo, eventType);
                eventBlobStore.saveEvent(eventInfo, rawJson);
            }
        } catch (DuplicateKeyException e) {
            final Query query = Query.query(Criteria.where("source").is(eventInfo.getSource()).and("eventId").is(eventInfo.getEventId()));
            return mongoOperations.findOne(query, EventInfo.class, eventType);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return eventInfo;
    }


    /**
     * Finds an event by its mongo _id Object ID
     *
     * @param id        the _id object ID
     * @param eventType the type of event that is the topic/collection of the event
     * @return the optional Document
     */
    public Optional<EventInfo> findById(final ObjectId id, final EventType eventType) {
        return Optional.ofNullable(mongoOperations.findById(id, EventInfo.class, getCollectionName(eventType)));
    }

    /**
     * Finds an event by its mongo _id Object ID
     *
     * @param eventId   the eventIf of the event to obtain
     * @param eventType the type of event that is the topic/collection of the event
     * @return the optional Document
     */
    public Optional<EventInfo> findByEventId(final long eventId, final EventType eventType) {
        return Optional.ofNullable(
                mongoOperations.findOne(
                        Query.query(Criteria.where(EventConstants.EVENT_ID).is(eventId)),
                        EventInfo.class,
                        getCollectionName(eventType)
                )
        );
    }

    /**
     * Obtains a page documents from the specified eventId onwards sorted by id (eventId)
     *
     * @param eventId   the first ID of the events to search obtain
     * @param eventType the type of event that is the topic/collection of the event
     * @param queryMap  the optional map of equality query parameters that match keys and values in a mongo document
     * @param pageable  the pagination information of how much data to obtain
     * @return a page of documents
     */
    public Page<EventInfo> findAllSince(final long eventId,
                                        final EventType eventType, Map<String, String> queryMap,
                                        final Pageable pageable) {

        var collectionName = getCollectionName(eventType);

        var criteria = buildCriteria(eventId, queryMap);

        var query = Query.query(criteria);

        // Obtain the total number of matching documents
        var total = mongoOperations.count(query, collectionName);

        // Obtain a page worth of matching documents
        var documents = mongoOperations.find(
                query.with(pageable).with(Sort.by(Sort.Direction.ASC, EventConstants._ID)),
                EventInfo.class,
                collectionName
        );

        // Return the page of document
        return new PageImpl<>(documents, pageable, total);
    }


    /**
     * Obtains a page documents from the specified eventId onwards sorted by id (eventId)
     *
     * @param id        the first _id of the events to search obtain
     * @param eventType the type of event that is the topic/collection of the event
     * @param pageable  the pagination information of how much data to obtain
     * @return a page of documents
     */
    public Page<EventInfo> findAllSince(final ObjectId id, final EventType eventType, final Pageable pageable) {

        var collectionName = getCollectionName(eventType);

        final Query query = Query.query(Criteria.where(EventConstants._ID).gte(id));

        // Obtain the total number of matching documents
        var total = mongoOperations.count(query, collectionName);

        // Obtain a page worth of matching documents
        var documents = mongoOperations.find(
                query.with(pageable).with(Sort.by(Sort.Direction.ASC, EventConstants._ID)),
                EventInfo.class,
                collectionName
        );

        // Return the page of document
        return new PageImpl<>(documents, pageable, total);
    }

    Optional<EventStream> findEventStreamById(final ObjectId id) {
        return Optional.of(eventBlobStore.getEventById(id));
    }

    /**
     * Obtains the API Version string for the specified event
     *
     * @param eventId   the ID of the event
     * @param eventType the type of event that is the topic/collection of the event
     * @return the version of the specified event
     */
    public Optional<String> getApiVersion(final long eventId, final EventType eventType) {
        var event = findByEventId(eventId, eventType);
        return event.map(EventInfo::getVersion);
    }

    /**
     * Obtains the EventId of the most recent event
     *
     * @return the event ID
     */
    public Optional<Long> getLastEventId(final EventType eventType) {

        var query = Query.query(Criteria.where(EventConstants.EVENT_ID).exists(true))
                .with(Sort.by(Sort.Direction.DESC, EventConstants.EVENT_ID))
                .limit(1);

        var documents = mongoOperations.find(query, Document.class, getCollectionName(eventType));

        if (documents.size() == 1) {
            var eventDocument = documents.get(0);
            var eventId = (Number) eventDocument.get(EventConstants.EVENT_ID);
            return Optional.ofNullable(eventId != null ? eventId.longValue() : null);
        }
        return Optional.empty();
    }


    /**
     * Creates the indexes for the collections used for events
     */
    void createIndexes() {

        // Create the common indexes for all topics
        for (EventType eventType : EventType.values()) {
            mongoOperations.getCollection(getCollectionName(eventType))
                    .createIndex(Indexes.ascending(EventConstants.TYPE, EventConstants.DATA_TYPE));

            final Bson put = new BasicDBObject(EventConstants.SOURCE, 1).append(EventConstants.EVENT_ID, 1);
            final IndexOptions source_eventId = new IndexOptions().name("source_eventId").unique(true);
            mongoOperations.getCollection(getCollectionName(eventType)).createIndex(put, source_eventId);
        }
    }

    private static Criteria buildCriteria(final long eventId, final Map<String, String> queryMap) {
        final Criteria criteria;

        if (eventId == 0) {
            criteria = new Criteria();
        } else {
            criteria = Criteria.where(EventConstants.EVENT_ID).gte(eventId);
        }

        if (queryMap != null) {
            queryMap.forEach((key, value) -> criteria.and(key).is(value));
        }
        return criteria;
    }

    /**
     * Obtains the collection name for a given eventType/topic.
     *
     * @param eventType the event type that is used as a kafka topic and mongo collection name
     * @return the collection name for the event type
     */
    private String getCollectionName(final EventType eventType) {
        return eventType.name().toLowerCase();
    }

    private boolean isNotVersionType(final EventInfo eventInfo) {
        return DataType.API_VERSION != DataType.of(eventInfo.getDataType());
    }
}


