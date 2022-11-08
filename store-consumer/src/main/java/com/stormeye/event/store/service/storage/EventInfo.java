package com.stormeye.event.store.service.storage;

import com.casper.sdk.model.event.DataType;
import com.casper.sdk.model.event.EventData;
import com.casper.sdk.model.event.EventType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


/**
 * @author ian@meywood.com
 */
@JsonDeserialize(using = EventInfoDeserializer.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@AllArgsConstructor
public class EventInfo {

    /** The optional ID of the event */
    private Long id;
    /** The type/topic of the event main, delpoys, sigs */
    private EventType eventType;
    /** The URL of the casper node that emitted the event */
    @Setter
    private String source;
    /** The type of the data: in the event e.g. DeployAdded etc */
    private DataType dataType;
    /** The version of the casper node when the event was emitted */
    private String version;
    /** The Event data to persist */
    private EventData data;
    /** The raw event JSON to allow for custom processing of the JSON */
    private TreeNode jsonData;

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
