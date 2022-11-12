package com.stormeye.event.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import com.casper.sdk.model.common.Digest;
import com.casper.sdk.model.key.PublicKey;
import com.stormeye.event.service.storage.domain.Block;

import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

/**
 * @author ian@meywood.com
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class BlockRepositoryTest {

    @Autowired
    private BlockRepository blockRepository;

    @BeforeEach
    void setUp() {
        blockRepository.deleteAll();
    }

    @Test
    void save() throws NoSuchAlgorithmException {

        final Date timestamp = new Date();

        final PublicKey proposer = PublicKey.fromTaggedHexString("017d96b9a63abcb61c870a4f55187a0a7ac24096bdb5fc585c12a686a4d892009e");
        final Block block = new Block(
                new Digest("5a91486c973deea304e26138206723278d9d269f4fe03bfc9e5fdb93e927236e"),
                new Digest("6c6aa63fb4e3e10f964e3be535d29b023902ace44483409e932ffd3cadfbf47b"),
                timestamp,
                new Digest("99a6cae10c5ab5b528e968378ead4bc8ef56a6613227e85e28845d9e398103ae"),
                1,
                2,
                3L,
                proposer,
                4L,
                1L
        );

        final Block saved = blockRepository.save(block);
        assertThat(saved.getId(), is(greaterThan(0L)));

        final Optional<Block> byId = blockRepository.findById(Objects.requireNonNull(saved.getId()));
        assertThat(byId.isPresent(), is(true));

        final Block found = byId.get();
        assertThat(found.getId(), is(saved.getId()));
        assertThat(found.getBlockHash(), is(new Digest("5a91486c973deea304e26138206723278d9d269f4fe03bfc9e5fdb93e927236e")));
        assertThat(found.getParentHash(), is(new Digest("6c6aa63fb4e3e10f964e3be535d29b023902ace44483409e932ffd3cadfbf47b")));
        assertThat(found.getTimestamp().getTime(), is(timestamp.getTime()));
        assertThat(found.getState(), is(new Digest("99a6cae10c5ab5b528e968378ead4bc8ef56a6613227e85e28845d9e398103ae")));
        assertThat(found.getDeployCount(), is(1L));
        assertThat(found.getTransferCount(), is(2L));
        assertThat(found.getEraId(), is(3L));
        assertThat(found.getProposer(), is(proposer));
        assertThat(found.getBlockHeight(), is(4L));
        assertThat(found.getBlockHeight(), is(4L));
    }

    @Test
    void findByBlockHash() throws NoSuchAlgorithmException {

        final Digest blockHash = new Digest("5a91486c973deea304e26138206723278d9d269f4fe03bfc9e5fdb93e927236e");
        final Block block = new Block(
                blockHash,
                new Digest("6c6aa63fb4e3e10f964e3be535d29b023902ace44483409e932ffd3cadfbf47b"),
                new Date(),
                new Digest("99a6cae10c5ab5b528e968378ead4bc8ef56a6613227e85e28845d9e398103ae"),
                1,
                2,
                3L,
                PublicKey.fromTaggedHexString("017d96b9a63abcb61c870a4f55187a0a7ac24096bdb5fc585c12a686a4d892009e"),
                4L,
                5L
        );

        blockRepository.save(block);
        assertThat(block.getId(), is(notNullValue()));
        assertThat(block.getEventId(), is(5L));

        Optional<Block> byBlockHash = blockRepository.findByBlockHash(blockHash);
        assertThat(byBlockHash.isPresent(), is(true));
        assertThat(byBlockHash.get().getBlockHash(), is(blockHash));
        assertThat(byBlockHash.get().getProposer(), is(block.getProposer()));

        // Search for non-existent block
        byBlockHash = blockRepository.findByBlockHash(new Digest("6c6aa63fb4e3e10f964e3be535d29b023902ace44483409e932ffd3cadfbf47e"));
        assertThat(byBlockHash.isPresent(), is(false));
    }
}
