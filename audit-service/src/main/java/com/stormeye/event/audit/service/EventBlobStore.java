package com.stormeye.event.audit.service;

import com.mongodb.client.gridfs.model.GridFSFile;
import com.stormeye.event.audit.execption.NotFoundException;
import com.stormeye.event.common.EventConstants;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsUpload;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.Objects;
import java.util.UUID;


/**
 * The service for storing and retrieving JSON Events to and from GridFS.
 *
 * @author ian@meywood.com
 */
@Service
class EventBlobStore {

    private static final String NOT_FOUND_MESSAGE = "Unable to find event with id ";
    private final Logger logger = LoggerFactory.getLogger(EventAuditService.class);
    private final GridFsOperations gridFsOperations;

    EventBlobStore(final GridFsOperations gridFsOperations) {
        this.gridFsOperations = gridFsOperations;
    }

    /**
     * Saves an event as JSON in GridFS
     *
     * @param eventInfo the metadata of the event
     * @param json      the data JSON
     * @return the ID of the stored grids file
     */
    EventInfo saveEvent(final EventInfo eventInfo, final byte[] json) {

        Objects.requireNonNull(eventInfo, "eventInfo cannot be null");
        Objects.requireNonNull(eventInfo.getId(), "_id cannot be null");

        final String filename = buildFilename(eventInfo);

        logger.debug("Saving event as : {}", filename);



        // Obtain the event ID
        final Document metadata = new Document()
                .append(EventConstants.TYPE, eventInfo.getEventType())
                .append(EventConstants.DATA_TYPE, eventInfo.getDataType())
                .append(EventConstants.SOURCE, eventInfo.getSource())
                .append(EventConstants.VERSION, eventInfo.getVersion())
                .append(EventConstants.BYTES, json.length);

        // Add the event ID if it exists
        if (eventInfo.getEventId() != null) {
            metadata.put("eventId", eventInfo.getEventId());
        }

        gridFsOperations.store(GridFsUpload.fromStream(new ByteArrayInputStream(json))
                .id(eventInfo.getId())
                .filename(filename)
                .contentType("application/json")
                .metadata(metadata)
                .build());

        return eventInfo;
    }

    /**
     * Obtains an input stream to a JSON Event from GridFS.
     *
     * @param id of the GridFs file to obtain
     * @return the input stream to read the file from
     */
    EventStream getEventById(final ObjectId id) {

        logger.debug("reading event {}", id);

        final GridFSFile gridFsFile = gridFsOperations.findOne(new Query(Criteria.where("_id").is(id)));

        if (gridFsFile == null) {
            throw new NotFoundException(NOT_FOUND_MESSAGE + id);
        }

        final GridFsResource resource = gridFsOperations.getResource(gridFsFile);

        if (!resource.exists()) {
            throw new NotFoundException(NOT_FOUND_MESSAGE + id);
        }
        return new EventStream(resource.getContent(), gridFsFile.getLength());
    }

    private String buildFilename(final EventInfo eventInfo) {
        return "/events/" + eventInfo.getEventType() + "/" + getUniqueId(eventInfo) + ".json";
    }

    private String getUniqueId(final EventInfo eventInfo) {
        if (eventInfo.getEventId() != null) {
            return Long.toString(eventInfo.getEventId());
        } else {
            // No ID provided on the event so generate one. This will only occur for ApiVersion events
            return UUID.randomUUID().toString();
        }
    }
}

