package com.stormeye.event.service.storage.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stormeye.event.service.storage.domain.Block;
import com.stormeye.event.utils.DateUtils;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.TimeZone;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringContains.containsString;

/**
 * Unit tests the {@link IsoDateTimeSerializer}.
 *
 * @author ian@meywood.com
 */
class IsoDateTimeSerializerTest {

    @Test
    void isoDateTimeSerializer() throws JsonProcessingException {

        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        Date timestamp = DateUtils.fromIso8601("2022-11-08T13:43:09.568Z");

        Block block = Block.builder().timestamp(timestamp).build();

        String json = new ObjectMapper().writeValueAsString(block);

        assertThat(json, containsString("\"timestamp\":\"2022-11-08T13:43:09.568Z\""));
    }
}