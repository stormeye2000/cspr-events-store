package com.stormeye.event.api.resource;

import com.casper.sdk.model.key.PublicKey;
import com.stormeye.event.repository.EraValidatorRepository;
import com.stormeye.event.service.storage.domain.EraValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for the EraValidatorResource.
 *
 * @author ian@meywood.com
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class EraValidatorResourceTest {

    @Autowired
    private EraValidatorRepository eraValidatorRepository;
    @Autowired
    private WebApplicationContext context;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() throws NoSuchAlgorithmException {

        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();

        eraValidatorRepository.deleteAll();

        eraValidatorRepository.save(EraValidator.builder()
                .eraId(1234L)
                .publicKey(PublicKey.fromTaggedHexString("01018525deae6091abccab6704a0fa44e12c495eec9e8fe6929862e1b75580e715"))
                .weight(BigInteger.valueOf(45678))
                .rewards(BigInteger.TEN)
                .hasEquivocation(true)
                .wasActive(true)
                .build());

        eraValidatorRepository.save(EraValidator.builder()
                .eraId(1234L)
                .publicKey(PublicKey.fromTaggedHexString("01018525deae6091abccab6704a0fa44e12c495eec9e8fe6929862e1b75580e716"))
                .weight(BigInteger.valueOf(45679))
                .rewards(BigInteger.TEN)
                .hasEquivocation(true)
                .wasActive(true)
                .build());

        eraValidatorRepository.save(EraValidator.builder()
                .eraId(1234L)
                .publicKey(PublicKey.fromTaggedHexString("01018525deae6091abccab6704a0fa44e12c495eec9e8fe6929862e1b75580e717"))
                .weight(BigInteger.valueOf(45679))
                .rewards(BigInteger.TWO)
                .hasEquivocation(true)
                .wasActive(true)
                .build());

        eraValidatorRepository.save(EraValidator.builder()
                .eraId(1234L)
                .publicKey(PublicKey.fromTaggedHexString("01018525deae6091abccab6704a0fa44e12c495eec9e8fe6929862e1b75580e718"))
                .weight(BigInteger.valueOf(45679))
                .rewards(BigInteger.TWO)
                .hasEquivocation(true)
                .wasActive(true)
                .build());

        eraValidatorRepository.save(EraValidator.builder()
                .eraId(1235L)
                .publicKey(PublicKey.fromTaggedHexString("01018525deae6091abccab6704a0fa44e12c495eec9e8fe6929862e1b75580e717"))
                .weight(BigInteger.valueOf(45679))
                .rewards(BigInteger.TWO)
                .hasEquivocation(true)
                .wasActive(true)
                .build());
    }


    @Test
    void getEraValidators() throws Exception {

        mockMvc.perform(get("/era-validators")
                        .param("page", "1")
                        .param("size", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.[*]", hasSize(3)))
                .andExpect(jsonPath("$.itemCount", is(5)))
                .andExpect(jsonPath("$.pageCount", is(2)))
                .andExpect(jsonPath("$.pageNumber", is(1)));
    }

    @Test
    void getEraValidatorsByEraId() throws Exception {

        mockMvc.perform(get("/era-validators/{eraId}",1234L)
                        .param("page", "1")
                        .param("size", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.[*]", hasSize(3)))
                .andExpect(jsonPath("$.itemCount", is(4)))
                .andExpect(jsonPath("$.pageCount", is(2)))
                .andExpect(jsonPath("$.pageNumber", is(1)))
                .andExpect(jsonPath("$.data[0].publicKey", is("01018525deae6091abccab6704a0fa44e12c495eec9e8fe6929862e1b75580e718")))
                .andExpect(jsonPath("$.data[2].publicKey", is("01018525deae6091abccab6704a0fa44e12c495eec9e8fe6929862e1b75580e716")));
    }

    @Test
    void getEraValidatorsByEraIdAscendingOrder() throws Exception {
        mockMvc.perform(get("/era-validators/{eraId}",1234L)
                        .param("page", "1")
                        .param("size", "3")
                        .param("order_direction", "ASC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.[*]", hasSize(3)))
                .andExpect(jsonPath("$.itemCount", is(4)))
                .andExpect(jsonPath("$.pageCount", is(2)))
                .andExpect(jsonPath("$.pageNumber", is(1)))
                //.andExpect(jsonPath("$.data[2].id", is(4)))
                .andExpect(jsonPath("$.data[0].publicKey", is("01018525deae6091abccab6704a0fa44e12c495eec9e8fe6929862e1b75580e715")))
                .andExpect(jsonPath("$.data[2].publicKey", is("01018525deae6091abccab6704a0fa44e12c495eec9e8fe6929862e1b75580e717")));
    }
}