package com.stormeye.event.api.resource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
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
import com.stormeye.event.repository.DeployRepository;
import com.stormeye.event.repository.TransferRepository;
import com.stormeye.event.service.storage.domain.Deploy;
import com.stormeye.event.service.storage.domain.Transfer;

import java.io.IOException;
import java.util.List;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class DeployResourceTest {

    private static final String DEPLOYS_JSON = "/deploys.json";
    private static final String TRANSFERS_JSON = "/transfers.json";
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private DeployRepository deployRepository;
    @Autowired
    private TransferRepository transferRepository;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() throws IOException {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
        deployRepository.deleteAll();
        transferRepository.deleteAll();
        createDeploysTestData();
        createDeploysTransfersTestData();
    }

    @Test
    void configuration() {
        assertThat(context, is(notNullValue()));
        assertThat(mockMvc, is(notNullValue()));
    }

    @Test
    void getDeploys() throws Exception {

        mockMvc.perform(get("/deploys")
                        .param("page", "1")
                        .param("order_direction", Sort.Direction.ASC.name())
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.[*]", hasSize(5)))
                .andExpect(jsonPath("$.data.[0].deployHash", is("ca8511484fc22464d39193f883bca47e9bb5f7d3d138f72df184fd79df7abcd7")))
                .andExpect(jsonPath("$.data.[0].blockHash", is("5ad1d4b73b18e34e12edd7dce18103d3c049750435e89963f08cad0248207b6c")))
                .andExpect(jsonPath("$.data[0].account", is("0202ed20f3a93b5386bc41b6945722b2bd4250c48f5fa0632adf546e2f3ff6f4ddee")))
                .andExpect(jsonPath("$.data.[0].cost", is(9987)))
                .andExpect(jsonPath("$.data.[0].errorMessage", is(nullValue())))
                .andExpect(jsonPath("$.data.[0].timestamp", is("2022-10-12T11:18:39.000+00:00")))
                .andExpect(jsonPath("$.data.[0].eventId", is(1)))

                .andExpect(jsonPath("$.data.[4].deployHash", is("abd185fad90ac3b171898a3a37d7927a3e45e74f9d7b43a0313d340607a8f1b6")))

                .andExpect(jsonPath("$.itemCount", is(30)))
                .andExpect(jsonPath("$.pageCount", is(6)))
                .andExpect(jsonPath("$.pageNumber", is(1)));
    }
    @Test
    void getDeploy() throws Exception {

        mockMvc.perform(get("/deploys/31da1700290a4a183dad0bd90421509016014419503d8953ae499cf1498a54a0"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.deployHash", is("31da1700290a4a183dad0bd90421509016014419503d8953ae499cf1498a54a0")))
                .andExpect(jsonPath("$.blockHash", is("8e30fb8c26c7ec6067a46924f67a034301e56f912c406abdd2ed7964637fc0d8")))
                .andExpect(jsonPath("$.account", is("020396133b3bbbfcf7d1961390f9449e2de5813523180376df361cb31a1ca965b576")))
                .andExpect(jsonPath("$.cost", is(70000)))
                .andExpect(jsonPath("$.errorMessage", is("failure error message")))
                .andExpect(jsonPath("$.timestamp", is("2022-11-08T11:18:39.000+00:00")))
                .andExpect(jsonPath("$.eventId", is(28)));

    }

    @Test
    void getDeploysTimestampSortDesc() throws Exception {

        mockMvc.perform(get("/deploys")
                        .param("page", "1")
                        .param("order_direction", Sort.Direction.DESC.name())
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.[*]", hasSize(5)))
                .andExpect(jsonPath("$.data.[0].deployHash", is("766446596bdbe297de33cafad687ba966f2fe15b966af4dce9f380dab73051a8")))

                .andExpect(jsonPath("$.data.[4].deployHash", is("e2cdc83b6a537f5e30936742a91ad01f5c389956e117320afeb5ea816110234a")))

                .andExpect(jsonPath("$.itemCount", is(30)))
                .andExpect(jsonPath("$.pageCount", is(6)))
                .andExpect(jsonPath("$.pageNumber", is(1)));
    }


    @Test
    void getDeploys2ndPage() throws Exception {

        mockMvc.perform(get("/deploys")
                        .param("page", "2")
                        .param("order_direction", Sort.Direction.ASC.name())
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.[*]", hasSize(5)))
                .andExpect(jsonPath("$.data.[0].deployHash", is("36b65cf635c2731e3ddc35380cdf6789fb822f84ac59196fae6f156c28a370a7")))

                .andExpect(jsonPath("$.data.[4].deployHash", is("3cd2d5e1e4e6b301bdacb6520369a47033a7d71bd0baf3e9ff296db235f519c0")))

                .andExpect(jsonPath("$.itemCount", is(30)))
                .andExpect(jsonPath("$.pageCount", is(6)))
                .andExpect(jsonPath("$.pageNumber", is(2)));
    }


    @Test
    void getDeploysTransfers() throws Exception {

        mockMvc.perform(get("/deploys/ca8511484fc22464d39193f883bca47e9bb5f7d3d138f72df184fd79df7abcd7/transfers")
                        .param("page", "1")
                        .param("size", "3"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.[*]", hasSize(3)))
                .andExpect(jsonPath("$.data.[0].transferId", is(7099695426227L)))
                .andExpect(jsonPath("$.data.[0].transferHash", is("0f11f6af6d1c4157ab00513d6997c3a8b7a37db2f3be3e597c7d1fbfc54fbcfd")))
                .andExpect(jsonPath("$.data[0].blockHash", is("3ee92f8737f0939a87046e94ee469bc12eaaca4086954488dad6fb762496de2b")))
                .andExpect(jsonPath("$.data.[0].fromAccount", is("01ba1dcfbe8dba48b88674bb007c000391c0ea36b5d80570c113a42a9823d702c2")))
                .andExpect(jsonPath("$.data.[0].toAccount", is("01efef1e78e645e6d4647eef856bd45f172a44ee5ded4980bc87e7d1026e74a19a")))
                .andExpect(jsonPath("$.data.[0].timestamp", is("2022-10-13T11:32:39.000+00:00")))
                .andExpect(jsonPath("$.data.[0].sourcePurse", is("uref-d81c4cc61e95836c7de2625cae9daca43eee5e2589730d3786f53faddb007ab1-007")))
                .andExpect(jsonPath("$.data.[0].targetPurse", is("uref-321785ed2d7ed7d35605a20d1e7f1ee8ad318d35585cbbec262b4b48f5c8ad54-004")))
                .andExpect(jsonPath("$.data.[0].amount", is(59702036)))

                .andExpect(jsonPath("$.data.[2].transferId", is(7099695426229L)))

                .andExpect(jsonPath("$.itemCount", is(15)))
                .andExpect(jsonPath("$.pageCount", is(5)))
                .andExpect(jsonPath("$.pageNumber", is(1)));
    }

    @Test
    void getDeploysTransfer() throws Exception {

        mockMvc.perform(get("/deploys/31da1700290a4a183dad0bd90421509016014419503d8953ae499cf1498a54a0/transfers")
                        .param("page", "1")
                        .param("size", "3"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.[*]", hasSize(1)))
                .andExpect(jsonPath("$.data.[0].transferId", is(7099695426242L)))
                .andExpect(jsonPath("$.data.[0].transferHash", is("0f11f6af6d1c4157ab00513d6997c3a8b7a37db2f3be3e597c7d1fbfc54fbcfd")))
                .andExpect(jsonPath("$.data.[0].fromAccount", is("01d6aceccfa3063684901d800b82e16682aaa163b9559985231591d04e43c0e14d")))
                .andExpect(jsonPath("$.data.[0].toAccount", is("0202f7a4631d1a25a57b62fd1fe323becd0e72407e9c8e0ce4cd5fc1b6d93abd22bc")))
                .andExpect(jsonPath("$.data.[0].timestamp", is("2022-10-13T11:18:39.000+00:00")))

                .andExpect(jsonPath("$.itemCount", is(1)))
                .andExpect(jsonPath("$.pageCount", is(1)))
                .andExpect(jsonPath("$.pageNumber", is(1)));
    }

    @Test
    void getDeploysTransfersByTimestampAsc() throws Exception {

        mockMvc.perform(get("/deploys/ca8511484fc22464d39193f883bca47e9bb5f7d3d138f72df184fd79df7abcd7/transfers")
                        .param("page", "1")
                        .param("size", "3")
                        .param("order_by", "timestamp")
                        .param("order_direction", Sort.Direction.ASC.name()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.[*]", hasSize(3)))
                .andExpect(jsonPath("$.data.[0].transferId", is(7099695426241L)))
                .andExpect(jsonPath("$.data.[0].transferHash", is("0f11f6af6d1c4157ab00513d6997c3a8b7a37db2f3be3e597c7d1fbfc54fbcfd")))

                .andExpect(jsonPath("$.data.[2].transferId", is(7099695426239L)))

                .andExpect(jsonPath("$.itemCount", is(15)))
                .andExpect(jsonPath("$.pageCount", is(5)))
                .andExpect(jsonPath("$.pageNumber", is(1)));
    }

    @Test
    void getDeploysTransfersByFrom() throws Exception {

        mockMvc.perform(get("/deploys/ca8511484fc22464d39193f883bca47e9bb5f7d3d138f72df184fd79df7abcd7/transfers")
                        .param("page", "1")
                        .param("size", "3")
                        .param("order_by", "fromAccount")
                        .param("order_direction", Sort.Direction.DESC.name()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.[*]", hasSize(3)))
                .andExpect(jsonPath("$.data.[0].fromAccount", is("03ba1dcfbe8dba48b88674bb007c000391c0ea36b5d80570c113a42a9823d702c2")))
                .andExpect(jsonPath("$.data.[0].transferHash", is("0f11f6af6d1c4157ab00513d6997c3a8b7a37db2f3be3e597c7d1fbfc54fbcfd")))

                .andExpect(jsonPath("$.data.[2].fromAccount", is("01ba1dcfbe8dba48b88674bb007c000391c0ea36b5d80570c113a42a9823d702c2")))

                .andExpect(jsonPath("$.itemCount", is(15)))
                .andExpect(jsonPath("$.pageCount", is(5)))
                .andExpect(jsonPath("$.pageNumber", is(1)));
    }

    @Test
    void getDeploysTransfersByTo() throws Exception {

        mockMvc.perform(get("/deploys/ca8511484fc22464d39193f883bca47e9bb5f7d3d138f72df184fd79df7abcd7/transfers")
                        .param("page", "1")
                        .param("size", "3")
                        .param("order_by", "toAccount")
                        .param("order_direction", Sort.Direction.DESC.name()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.[*]", hasSize(3)))
                .andExpect(jsonPath("$.data.[0].toAccount", is("04efef1e78e645e6d4647eef856bd45f172a44ee5ded4980bc87e7d1026e74a19a")))
                .andExpect(jsonPath("$.data.[0].transferHash", is("ae4ab073e8f9c0a3348aacf226a08391ab38a194bf865cf46b0a1f441809d85a")))

                .andExpect(jsonPath("$.data.[2].toAccount", is("02efef1e78e645e6d4647eef856bd45f172a44ee5ded4980bc87e7d1026e74a19a")))

                .andExpect(jsonPath("$.itemCount", is(15)))
                .andExpect(jsonPath("$.pageCount", is(5)))
                .andExpect(jsonPath("$.pageNumber", is(1)));
    }

    @Test
    void getDeploysTransfersByTransferId() throws Exception {

        mockMvc.perform(get("/deploys/ca8511484fc22464d39193f883bca47e9bb5f7d3d138f72df184fd79df7abcd7/transfers")
                        .param("page", "1")
                        .param("size", "3")
                        .param("order_by", "transferId")
                        .param("order_direction", Sort.Direction.ASC.name()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.[*]", hasSize(3)))
                .andExpect(jsonPath("$.data.[0].transferId", is(7099695426226L)))
                .andExpect(jsonPath("$.data.[0].transferHash", is("0f11f6af6d1c4157ab00513d6997c3a8b7a37db2f3be3e597c7d1fbfc54fbcfd")))

                .andExpect(jsonPath("$.data.[2].transferId", is(7099695426228L)))

                .andExpect(jsonPath("$.itemCount", is(15)))
                .andExpect(jsonPath("$.pageCount", is(5)))
                .andExpect(jsonPath("$.pageNumber", is(1)));
    }

    @Test
    void getDeploysTransfersByAmount() throws Exception {

        mockMvc.perform(get("/deploys/ca8511484fc22464d39193f883bca47e9bb5f7d3d138f72df184fd79df7abcd7/transfers")
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

                .andExpect(jsonPath("$.itemCount", is(15)))
                .andExpect(jsonPath("$.pageCount", is(5)))
                .andExpect(jsonPath("$.pageNumber", is(1)));
    }


    public void createDeploysTestData() throws IOException {

        var in = BlockResourceTest.class.getResourceAsStream(DEPLOYS_JSON);
        var deploys = new ObjectMapper().readValue(in, new TypeReference<List<Deploy>>() {
        });

        assertThat(deploys, is(notNullValue()));
        assertThat(deploys, hasSize(30));

        deployRepository.saveAll(deploys);

        assertThat(deployRepository.count(), is(30L));
    }
    public void createDeploysTransfersTestData() throws IOException {

        var in = BlockResourceTest.class.getResourceAsStream(TRANSFERS_JSON);
        var transfers = new ObjectMapper().readValue(in, new TypeReference<List<Transfer>>() {
        });

        assertThat(transfers, is(notNullValue()));
        assertThat(transfers, hasSize(16));

        transferRepository.saveAll(transfers);

        assertThat(transferRepository.count(), is(16L));
    }
}
