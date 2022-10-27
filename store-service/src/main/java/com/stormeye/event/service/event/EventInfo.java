package com.stormeye.event.service.event;

import com.casper.sdk.model.event.DataType;
import com.casper.sdk.model.event.EventData;
import com.casper.sdk.model.event.EventType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


/**
 * @author ian@meywood.com
 */
@JsonDeserialize(using = EventInfoDeserializer.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventInfo {

    /** The optional ID of the event */
    private Long id;
    /** The type/topic of the event main, delpoys, sigs */
    private EventType eventType;
    /** The URL of the casper node that emitted the event */
    private String source;
    /** The type of the data: in the event e.g. DeployAdded etc */
    private DataType dataType;
    /** The version of the casper node when the event was emitted */
    private String version;
    /** The raw event JSON, persisted in GridFS not a mongo collection */
    private EventData data;

    public EventInfo() {
        this(null, null, null, null, null, null);
    }


    public EventInfo(final Long id,
                     final EventType eventType,
                     final String source,
                     final DataType dataType,
                     final String version,
                     final EventData data) {
        this.id = id;
        this.eventType = eventType;
        this.source = source;
        this.dataType = dataType;
        this.version = version;
        this.data = data;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public EventData getData() {
        return data;
    }

    public void setData(EventData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "{" +
                "id='" + id + '\'' +
                ", eventType='" + eventType + '\'' +
                ", source='" + source + '\'' +
                ", dataType='" + dataType + '\'' +
                ", version='" + version + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}
