package com.stormeye.event.audit.service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Object that provides the metadata for a JSON event.
 *
 * @author ian@meywood.com
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Document
@Getter
@Setter
@NoArgsConstructor
public class AuditEventInfo {

    /** The internal Mongo ID of the GridFS file that stores the Event JSON */
    @Id
    private ObjectId id;
    /** The type/topic of the event main, delpoys, sigs */
    @Field
    private String eventType;
    /** The URL of the casper node that emitted the event */
    @Field
    private String source;
    /** The type of the data: in the event e.g. DeployAdded etc */
    @Field
    private String dataType;
    /** The optional ID of the event */
    @Field
    private Long eventId;
    /** The version of the casper node when the event was emitted */
    @Field
    private String version;
    /** The raw event JSON, persisted in GridFS not a mongo collection */
    @Transient
    private String data;
    /** The length of the RAW JSON in bytes */
    @Field
    private int bytes;

    @JsonCreator
    public AuditEventInfo(@JsonProperty(value = "_id") final ObjectId id,
                          @JsonProperty(value = "type", required = true) final String eventType,
                          @JsonProperty(value = "source", required = true) final String source,
                          @JsonProperty(value = "dataType", required = true) final String dataType,
                          @JsonProperty(value = "id") final Long eventId,
                          @JsonProperty(value = "version") final String version,
                          @JsonProperty(value = "data") final String data,
                          @JsonProperty(value = "bytes") final int bytes) {
        this.id = id;
        this.eventType = eventType;
        this.source = source;
        this.dataType = dataType;
        this.eventId = eventId;
        this.version = version;
        this.data = data;
        this.bytes = bytes;
    }


    public int getBytes() {
        if (bytes == 0 && data != null && data.length() > 0) {
            bytes = data.length();
        }
        return bytes;
    }

    @Override
    public String toString() {
        return "{" +
                "id='" + id + '\'' +
                ", eventType='" + eventType + '\'' +
                ", source='" + source + '\'' +
                ", dataType='" + dataType + '\'' +
                ", eventId='" + eventId + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
