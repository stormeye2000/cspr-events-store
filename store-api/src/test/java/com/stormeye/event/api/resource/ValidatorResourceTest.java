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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stormeye.event.repository.BlockRepository;
import com.stormeye.event.repository.DelegatorRewardRepository;
import com.stormeye.event.repository.ValidatorRewardRepository;
import com.stormeye.event.service.storage.domain.Block;
import com.stormeye.event.service.storage.domain.DelegatorReward;
import com.stormeye.event.service.storage.domain.ValidatorReward;

import java.io.IOException;
import java.util.List;

/**
 * Tests the validator REST APIs
 *
 * @author ian@meywood.com
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class ValidatorResourceTest {

    private static final String VALIDATOR_REWARDS_JSON = "/validator-rewards.json";

    private static final String DELEGATOR_REWARDS_JSON = "/delegator-rewards.json";
    private static final String BLOCKS_JSON = "/blocks.json";
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private BlockRepository blockRepository;
    @Autowired
    private DelegatorRewardRepository delegatorRewardRepository;
    @Autowired
    private ValidatorRewardRepository validatorRewardRepository;
    private MockMvc mockMvc;

    private final String rootPath = "/api/v1";

    @BeforeEach
    void setUp() throws IOException {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
        blockRepository.deleteAll();
        delegatorRewardRepository.deleteAll();
        validatorRewardRepository.deleteAll();
        createTestData();
    }

    @Test
    void getValidatorRewards() throws Exception {
        mockMvc.perform(get(rootPath + "/validators/{publicKey}/rewards", "01018525deae6091abccab6704a0fa44e12c495eec9e8fe6929862e1b75580e715")
                        .param("page", "1")
                        .param("size", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.[*]", hasSize(3)))
                .andExpect(jsonPath("$.itemCount", is(5)))
                .andExpect(jsonPath("$.pageCount", is(2)))
                .andExpect(jsonPath("$.pageNumber", is(1)))
                .andExpect(jsonPath("$.data.[0].eraId", is(6708)))
                .andExpect(jsonPath("$.data.[0].publicKey", is("01018525deae6091abccab6704a0fa44e12c495eec9e8fe6929862e1b75580e715")))
                .andExpect(jsonPath("$.data.[0].amount", is(142729418)))
                .andExpect(jsonPath("$.data.[0].timestamp", is("2022-11-12T13:49:09.568+00:00")));
    }

    @Test
    void getTotalValidatorRewards() throws Exception {
        mockMvc.perform(get(rootPath + "/validators/{publicKey}/total-rewards", "01018525deae6091abccab6704a0fa44e12c495eec9e8fe6929862e1b75580e715"))
                .andExpect(status().isOk())
                .andExpect(content().string("713647080"));

        mockMvc.perform(get(rootPath + "/validators/{publicKey}/total-rewards", "01018525deae6091abccab6704a0fa44e12c495eec9e8fe6929862e1b75580e71f"))
                .andExpect(status().isOk())
                .andExpect(content().string("0"));

    }

    @Test
    void getTotalValidatorDelegatorRewards() throws Exception {
        mockMvc.perform(get(rootPath + "/validators/{publicKey}/total-delegator-rewards", "01018525deae6091abccab6704a0fa44e12c495eec9e8fe6929862e1b75580e716"))
                .andExpect(status().isOk())
                .andExpect(content().string("1299105916"));

        mockMvc.perform(get(rootPath + "/validators/{publicKey}/total-delegator-rewards", "01018525deae6091abccab6704a0fa44e12c495eec9e8fe6929862e1b75580e717"))
                .andExpect(status().isOk())
                .andExpect(content().string("0"));
    }

    @Test
    void getValidatorBlocks() throws Exception {

        mockMvc.perform(get(rootPath + "/validators/{publicKey}/blocks", "015fd964620f98e551065079e142840dac3fb25bd97a0d4722411cb439f9247d72")
                        .param("page", "1")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.[*]", hasSize(2)))
                .andExpect(jsonPath("$.itemCount", is(3)))
                .andExpect(jsonPath("$.pageCount", is(2)))
                .andExpect(jsonPath("$.pageNumber", is(1)))
                .andExpect(jsonPath("$.data.[0].proposer", is("015fd964620f98e551065079e142840dac3fb25bd97a0d4722411cb439f9247d72")))
                .andExpect(jsonPath("$.data.[0].blockHash", is("8d0c807b66c73746a457782ea56c127c6820e69bd9802ee3c53200199d2aa3cb")))
                .andExpect(jsonPath("$.data.[1].blockHash", is("0233ef142b864a4d8d125ae402ae62b2fe4993a1cfa1c3d4a5c5dd6c61c8ee70")));

        // Get the next page
        mockMvc.perform(get(rootPath + "/validators/{publicKey}/blocks", "015fd964620f98e551065079e142840dac3fb25bd97a0d4722411cb439f9247d72")
                        .param("page", "2")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.[*]", hasSize(1)))
                .andExpect(jsonPath("$.itemCount", is(3)))
                .andExpect(jsonPath("$.pageCount", is(2)))
                .andExpect(jsonPath("$.pageNumber", is(2)))
                .andExpect(jsonPath("$.data.[0].proposer", is("015fd964620f98e551065079e142840dac3fb25bd97a0d4722411cb439f9247d72")))
                .andExpect(jsonPath("$.data.[0].blockHash", is("5ff2718f75cc80bc7382e1ced32c23cf882fa88dc9ae0cc33161fe7d098929af")));
    }

    private void createTestData() throws IOException {
        var in = ValidatorResourceTest.class.getResourceAsStream(VALIDATOR_REWARDS_JSON);
        var validatorRewards = new ObjectMapper().readValue(in, new TypeReference<List<ValidatorReward>>() {
        });

        assertThat(validatorRewards, is(notNullValue()));
        assertThat(validatorRewards, hasSize(7));

        validatorRewardRepository.saveAll(validatorRewards);
        assertThat(validatorRewardRepository.count(), is(7L));
        in = ValidatorResourceTest.class.getResourceAsStream(BLOCKS_JSON);

       var blocks = new ObjectMapper().readValue(in, new TypeReference<List<Block>>() {});
       blockRepository.saveAll(blocks);
       assertThat(blockRepository.count(), is(30L));

        in = ValidatorResourceTest.class.getResourceAsStream(DELEGATOR_REWARDS_JSON);
        var delegatorRewards = new ObjectMapper().readValue(in, new TypeReference<List<DelegatorReward>>() {
        });
        delegatorRewardRepository.saveAll(delegatorRewards);
        assertThat(delegatorRewardRepository.count(), is(7L));
    }
}
