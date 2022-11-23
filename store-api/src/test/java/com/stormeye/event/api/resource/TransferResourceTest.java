package com.stormeye.event.api.resource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stormeye.event.repository.TransferRepository;
import com.stormeye.event.service.storage.domain.Transfer;

import java.io.IOException;
import java.util.List;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class TransferResourceTest {

    private static final String TRANSFERS_JSON = "/transfers.json";
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private TransferRepository transferRepository;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() throws IOException {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
        transferRepository.deleteAll();
        createTransfersTestData();
    }

    @Test
    void configuration() {
        assertThat(context, is(notNullValue()));
        assertThat(mockMvc, is(notNullValue()));
    }

    @Test
    void getTransfers() throws Exception {

        mockMvc.perform(get("/transfers")
                .param("page", "1")
                .param("order_direction", Sort.Direction.ASC.name())
                .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.[*]", hasSize(5)))
                .andExpect(jsonPath("$.data.[0].transferId", is(7099695426242L)))
                .andExpect(jsonPath("$.data.[0].transferHash", is("0f11f6af6d1c4157ab00513d6997c3a8b7a37db2f3be3e597c7d1fbfc54fbcfd")))
                .andExpect(jsonPath("$.data.[0].amount", is(89702036)))

                .andExpect(jsonPath("$.data.[4].transferId", is(7099695426238L)))
                .andExpect(jsonPath("$.data.[4].amount", is(79702036)))

                .andExpect(jsonPath("$.itemCount", is(16)))
                .andExpect(jsonPath("$.pageCount", is(4)))
                .andExpect(jsonPath("$.pageNumber", is(1)));

    }

    @Test
    void getTransfersByFromAccount() throws Exception {

        mockMvc.perform(get("/transfers")
                .param("page", "1")
                .param("order_by", "fromAccount")
                .param("order_direction", Sort.Direction.DESC.name())
                .param("size", "3"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.[*]", hasSize(3)))
                .andExpect(jsonPath("$.data.[0].fromAccount", is("03ba1dcfbe8dba48b88674bb007c000391c0ea36b5d80570c113a42a9823d702c2")))
                .andExpect(jsonPath("$.data.[0].transferHash", is("0f11f6af6d1c4157ab00513d6997c3a8b7a37db2f3be3e597c7d1fbfc54fbcfd")))

                .andExpect(jsonPath("$.data.[2].fromAccount", is("01d6aceccfa3063684901d800b82e16682aaa163b9559985231591d04e43c0e14d")))

                .andExpect(jsonPath("$.itemCount", is(16)))
                .andExpect(jsonPath("$.pageCount", is(6)))
                .andExpect(jsonPath("$.pageNumber", is(1)));

    }

    @Test
    void getTransfersByToAccount() throws Exception {

        mockMvc.perform(get("/transfers")
                .param("page", "1")
                .param("order_by", "toAccount")
                .param("order_direction", Sort.Direction.DESC.name())
                .param("size", "3"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.[*]", hasSize(3)))
                .andExpect(jsonPath("$.data.[0].toAccount", is("04efef1e78e645e6d4647eef856bd45f172a44ee5ded4980bc87e7d1026e74a19a")))
                .andExpect(jsonPath("$.data.[0].transferHash", is("ae4ab073e8f9c0a3348aacf226a08391ab38a194bf865cf46b0a1f441809d85a")))

                .andExpect(jsonPath("$.data.[2].toAccount", is("02efef1e78e645e6d4647eef856bd45f172a44ee5ded4980bc87e7d1026e74a19a")))

                .andExpect(jsonPath("$.itemCount", is(16)))
                .andExpect(jsonPath("$.pageCount", is(6)))
                .andExpect(jsonPath("$.pageNumber", is(1)));

    }

    @Test
    void getTransfersByTransferId() throws Exception {

        mockMvc.perform(get("/transfers")
                .param("page", "1")
                        .param("order_by", "transferId")
                        .param("order_direction", Sort.Direction.ASC.name())
                        .param("size", "3"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.[*]", hasSize(3)))
                .andExpect(jsonPath("$.data.[0].transferId", is(7099695426226L)))
                .andExpect(jsonPath("$.data.[0].transferHash", is("0f11f6af6d1c4157ab00513d6997c3a8b7a37db2f3be3e597c7d1fbfc54fbcfd")))

                .andExpect(jsonPath("$.data.[2].transferId", is(7099695426228L)))

                .andExpect(jsonPath("$.itemCount", is(16)))
                .andExpect(jsonPath("$.pageCount", is(6)))
                .andExpect(jsonPath("$.pageNumber", is(1)));

    }

    @Test
    void getTransfersByAmount() throws Exception {

        mockMvc.perform(get("/transfers")
                .param("page", "1")
                        .param("size", "3")
                        .param("order_by", "amount")
                        .param("order_direction", Sort.Direction.ASC.name()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.[*]", hasSize(3)))
                .andExpect(jsonPath("$.data.[0].amount", is(49702036)))
                .andExpect(jsonPath("$.data.[0].transferHash", is("0f11f6af6d1c4157ab00513d6997c3a8b7a37db2f3be3e597c7d1fbfc54fbcfd")))

                .andExpect(jsonPath("$.data.[2].amount", is(69702036)))

                .andExpect(jsonPath("$.itemCount", is(16)))
                .andExpect(jsonPath("$.pageCount", is(6)))
                .andExpect(jsonPath("$.pageNumber", is(1)));

    }

    public void createTransfersTestData() throws IOException {

        var in = BlockResourceTest.class.getResourceAsStream(TRANSFERS_JSON);
        var transfers = new ObjectMapper().readValue(in, new TypeReference<List<Transfer>>() {
        });

        assertThat(transfers, is(notNullValue()));
        assertThat(transfers, hasSize(16));

        transferRepository.saveAll(transfers);

        assertThat(transferRepository.count(), is(16L));
    }

}
