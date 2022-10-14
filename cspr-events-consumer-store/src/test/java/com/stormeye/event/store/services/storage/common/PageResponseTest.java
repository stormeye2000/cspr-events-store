package com.stormeye.event.store.services.storage.common;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stormeye.event.store.services.storage.block.domain.Block;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Unit tests for the {@link PageResponse} used by the REST APIs to provide a page of data with context info
 *
 * @author ian@meywood.com
 */
class PageResponseTest {

    private static final String BLOCKS_JSON = "/com/stormeye/event/store/services/storage/block/blocks.json";

    private PageResponse<Block> pageResponse;


    @BeforeEach
    void setUp() throws IOException {

        var in = PageResponseTest.class.getResourceAsStream(BLOCKS_JSON);
        var blocks = new ObjectMapper().readValue(in, new TypeReference<List<Block>>() {
        });

        pageResponse = new PageResponse<>(new PageImpl<>(blocks.subList(0, 5), PageRequest.of(0, 5), blocks.size()));
    }

    @Test
    void getData() {
        assertThat(pageResponse.getData().size(), is(5));
    }

    @Test
    void getPageCount() {
        assertThat(pageResponse.getPageCount(), is(6L));
    }

    @Test
    void getItemCount() {
        assertThat(pageResponse.getItemCount(), is(30L));
    }

    @Test
    void getNumber() {
        assertThat(pageResponse.getPageNumber(), is(1));
    }
}
