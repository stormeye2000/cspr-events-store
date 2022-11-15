package com.stormeye.event.api.resource;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stormeye.event.repository.DelegatorRewardRepository;
import com.stormeye.event.service.storage.domain.DelegatorReward;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Delegator REST API tests.
 *
 * @author ian@meywood.com
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class DelegatorResourceTest {

    private static final String DELEGATOR_REWARDS_JSON = "/delegator-rewards.json";
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private DelegatorRewardRepository delegatorRewardRepository;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() throws IOException {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
        delegatorRewardRepository.deleteAll();
        createTestData();
    }

    @Test
    void getDelegatorRewards() throws Exception {
        mockMvc.perform(get("/delegators/{publicKey}/rewards", "01018525deae6091abccab6704a0fa44e12c495eec9e8fe6929862e1b75580e715")
                        .param("page", "1")
                        .param("size", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.[*]", hasSize(3)))
                .andExpect(jsonPath("$.itemCount", is(5)))
                .andExpect(jsonPath("$.pageCount", is(2)))
                .andExpect(jsonPath("$.pageNumber", is(1)))
                .andExpect(jsonPath("$.data.[0].eraId", is(6708)))
                .andExpect(jsonPath("$.data.[0].publicKey", is("01018525deae6091abccab6704a0fa44e12c495eec9e8fe6929862e1b75580e715")))
                .andExpect(jsonPath("$.data.[0].validatorPublicKey", is("01018525deae6091abccab6704a0fa44e12c495eec9e8fe6929862e1b75580e716")))
                .andExpect(jsonPath("$.data.[0].amount", is(242729418)))
                .andExpect(jsonPath("$.data.[0].timestamp", is("2022-11-12T13:49:09.568+00:00")));
    }

    @Test
    void getTotalDelegatorRewards() throws Exception {
        mockMvc.perform(get("/delegators/{publicKey}/total-rewards", "01018525deae6091abccab6704a0fa44e12c495eec9e8fe6929862e1b75580e715"))
                .andExpect(status().isOk())
                .andExpect(content().string("1013647080"));
    }

    private void createTestData() throws IOException {
        var in = DelegatorResourceTest.class.getResourceAsStream(DELEGATOR_REWARDS_JSON);
        var delegatorRewards = new ObjectMapper().readValue(in, new TypeReference<List<DelegatorReward>>() {
        });

        delegatorRewardRepository.saveAll(delegatorRewards);
        assertThat(delegatorRewardRepository.count(), is(7L));
    }
}