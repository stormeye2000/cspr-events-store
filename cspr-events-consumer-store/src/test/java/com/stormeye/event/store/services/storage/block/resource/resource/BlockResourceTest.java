package com.stormeye.event.store.services.storage.block.resource.resource;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stormeye.event.store.services.storage.block.domain.Block;
import com.stormeye.event.store.services.storage.block.repository.BlockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author ian@meywood.com
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class BlockResourceTest {

    private static final String BLOCKS_JSON = "/com/stormeye/event/store/services/storage/block/blocks.json";

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private BlockRepository blockRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() throws IOException {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
        //  this.mockMvc.getDispatcherServlet().setThrowExceptionIfNoHandlerFound(true);

        blockRepository.deleteAll();

        createTestData();
    }

    @Test
    void configuration() {
        assertThat(context, is(notNullValue()));
        assertThat(mockMvc, is(notNullValue()));
    }


    @Test
    void getBlocks() throws Exception {

        mockMvc.perform(get("/blocks")
                        .param("page", "1")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.[*]", hasSize(5)))
                .andExpect(jsonPath("$.data.[0].blockHash", is("844bfa519039cbcaed0949dfb80b7ba244d38813e89950832b0c2a2a3063de19")))
                .andExpect(jsonPath("$.data.[0].parentHash", is("8d0c807b66c73746a457782ea56c127c6820e69bd9802ee3c53200199d2aa3cb")))
                .andExpect(jsonPath("$.data.[0].eraId", is(6704)))
                .andExpect(jsonPath("$.data.[0].proposer", is("014b466f5c6c87bb1d2566d166120e320a724231374cd0775e0e347afed70a4745")))
                .andExpect(jsonPath("$.data.[0].deployCount", is(0)))
                .andExpect(jsonPath("$.data.[0].transferCount", is(0)))
                .andExpect(jsonPath("$.data.[0].blockHeight", is(1175543)))

                .andExpect(jsonPath("$.data.[4].blockHash", is("3f9a2258144a85a4dfa2817cbe2a2c75f8775a939f416a5f96be96a41f0c94bf")))

                .andExpect(jsonPath("$.itemCount", is(30)))
                .andExpect(jsonPath("$.pageCount", is(6)))
                .andExpect(jsonPath("$.pageNumber", is(1)));
    }

    @Test
    void get2ndPage() throws Exception {

        mockMvc.perform(get("/blocks")
                        .param("page", "2")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.[*]", hasSize(5)))
                .andExpect(jsonPath("$.data.[0].blockHash", is("50432875b6ed13cc9d45df8e818e744ce1b65a010ca3b222179ff65314919eaa")))

                .andExpect(jsonPath("$.data.[4].blockHash", is("0865e6c790a0b609c3df5f991f25e429905a71ac9ff3a473479f75b9fad3d816")))

                .andExpect(jsonPath("$.itemCount", is(30)))
                .andExpect(jsonPath("$.pageCount", is(6)))
                .andExpect(jsonPath("$.pageNumber", is(2)));
    }

    @Test
    void sortTimestampAscending() throws Exception {

        mockMvc.perform(get("/blocks")
                        .param("page", "1")
                        .param("size", "5")
                        .param("order_by", "timestamp")
                        .param("order_direction", Sort.Direction.ASC.name()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.itemCount", is(30)))
                .andExpect(jsonPath("$.pageCount", is(6)))
                .andExpect(jsonPath("$.pageNumber", is(1)))
                .andExpect(jsonPath("$.data.[0].blockHash", is("b2b952474e15c2ff3136ea742c1c2156ee7ab2495cb9a8bc2fd529b4f47da7f5")))
                .andExpect(jsonPath("$.data.[4].blockHash", is("771898057312a00e18ab600f63920fd9846821ae74d265b2ddd81b20c1bc0794")));
    }


    @Test
    void sortEraDescending() throws Exception {

        mockMvc.perform(get("/blocks")
                        .param("page", "1")
                        .param("size", "5")
                        .param("order_by", "eraId")
                        .param("order_direction", Sort.Direction.DESC.name()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.itemCount", is(30)))
                .andExpect(jsonPath("$.pageCount", is(6)))
                .andExpect(jsonPath("$.pageNumber", is(1)))
                .andExpect(jsonPath("$.data.[0].blockHash", is("b2b952474e15c2ff3136ea742c1c2156ee7ab2495cb9a8bc2fd529b4f47da7f5")))
                .andExpect(jsonPath("$.data.[4].blockHash", is("771898057312a00e18ab600f63920fd9846821ae74d265b2ddd81b20c1bc0794")));
    }

    @Test
    void sortBlockHeightAscending() throws Exception {

        mockMvc.perform(get("/blocks")
                        .param("page", "1")
                        .param("size", "5")
                        .param("order_by", "blockHeight")
                        .param("order_direction", Sort.Direction.ASC.name()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.itemCount", is(30)))
                .andExpect(jsonPath("$.pageCount", is(6)))
                .andExpect(jsonPath("$.pageNumber", is(1)))
                .andExpect(jsonPath("$.data.[0].blockHash", is("b2b952474e15c2ff3136ea742c1c2156ee7ab2495cb9a8bc2fd529b4f47da7f5")))
                .andExpect(jsonPath("$.data.[4].blockHash", is("771898057312a00e18ab600f63920fd9846821ae74d265b2ddd81b20c1bc0794")));
    }


    public void createTestData() throws IOException {

        var in = BlockResourceTest.class.getResourceAsStream(BLOCKS_JSON);
        var blocks = new ObjectMapper().readValue(in, new TypeReference<List<Block>>() {
        });

        assertThat(blocks, is(notNullValue()));
        assertThat(blocks, hasSize(30));

        blockRepository.saveAll(blocks);

        assertThat(blockRepository.count(), is(30L));
    }
}