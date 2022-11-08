package com.stormeye.event.store.service.storage;

import com.casper.sdk.model.event.DataType;
import com.casper.sdk.model.event.EventType;
import com.casper.sdk.model.event.blockadded.BlockAdded;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;

/**
 * @author ian@meywood.com
 */
class EventInfoDeserializerTest {

    private static final String BLOCK_ADDED_JSON = "/kafka-data/kafka-single-events-main.json";
    private static final String VERSION_JSON = "/kafka-data/single-version-event.json";

    @Test
    void deserializeVersionEvent() throws Exception {

        var in = EventInfoDeserializerTest.class.getResourceAsStream(VERSION_JSON);
        EventInfo eventInfo = new ObjectMapper().readValue(in, EventInfo.class);

        assertThat(eventInfo, is(notNullValue()));
        assertThat(eventInfo.getId(), is(nullValue()));
        assertThat(eventInfo.getEventType(), is(EventType.MAIN));
        assertThat(eventInfo.getDataType(), is(DataType.API_VERSION));
        assertThat(eventInfo.getSource(), is("http://65.21.235.219:9999"));
        assertThat(eventInfo.getVersion(), is("1.4.7"));
    }


    @Test
    //@Disabled
    void deserializeBlocAddedEvent() throws Exception {

        var in = EventInfoDeserializerTest.class.getResourceAsStream(BLOCK_ADDED_JSON);

        EventInfo eventInfo = new ObjectMapper().readValue(in, EventInfo.class);

        assertThat(eventInfo, is(notNullValue()));
        assertThat(eventInfo.getId(), is(65027303L));
        assertThat(eventInfo.getEventType(), is(EventType.MAIN));
        assertThat(eventInfo.getDataType(), is(DataType.BLOCK_ADDED));
        assertThat(eventInfo.getSource(), is("http://65.21.235.219:9999"));
        assertThat(eventInfo.getVersion(), is("1.0.0"));
        assertThat(eventInfo.getData(), instanceOf(BlockAdded.class));

        BlockAdded blockAdded = (BlockAdded) eventInfo.getData();
        assertThat(blockAdded.getBlockHash().toString(), is("5a91486c973deea304e26138206723278d9d269f4fe03bfc9e5fdb93e927236e"));
    }

}