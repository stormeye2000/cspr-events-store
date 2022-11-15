package com.stormeye.event.repository;

import com.casper.sdk.model.common.Digest;
import com.casper.sdk.model.key.PublicKey;
import com.stormeye.event.service.storage.domain.Block;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestPropertySource;

import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;

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
        assertThat(found.getId(), is(1L));
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

    @Test
    void findByProposer() throws NoSuchAlgorithmException {

        final Date timestamp = new Date();
        final PublicKey proposer = PublicKey.fromTaggedHexString("017d96b9a63abcb61c870a4f55187a0a7ac24096bdb5fc585c12a686a4d892009e");

        blockRepository.save(Block.builder()
                .blockHash(new Digest("5a91486c973deea304e26138206723278d9d269f4fe03bfc9e5fdb93e927236e"))
                .parentHash(new Digest("6c6aa63fb4e3e10f964e3be535d29b023902ace44483409e932ffd3cadfbf47b"))
                .timestamp(timestamp)
                .state(new Digest("99a6cae10c5ab5b528e968378ead4bc8ef56a6613227e85e28845d9e398103ae"))
                .deployCount(1)
                .transferCount(2)
                .eraId(3)
                .proposer(proposer)
                .blockHeight(4L)
                .eventId(1L)
                .build()
        );

        blockRepository.save(Block.builder()
                .blockHash(new Digest("5a91486c973deea304e26138206723278d9d269f4fe03bfc9e5fdb93e927236f"))
                .parentHash(new Digest("5a91486c973deea304e26138206723278d9d269f4fe03bfc9e5fdb93e927236e"))
                .timestamp(new Date(timestamp.getTime() + 1000))
                .state(new Digest("99a6cae10c5ab5b528e968378ead4bc8ef56a6613227e85e28845d9e398103af"))
                .deployCount(2)
                .transferCount(3)
                .eraId(3)
                .proposer(proposer)
                .blockHeight(5L)
                .eventId(2L)
                .build()
        );

        blockRepository.save(Block.builder()
                .blockHash(new Digest("5a91486c973deea304e26138206723278d9d269f4fe03bfc9e5fdb93e9272370"))
                .parentHash(new Digest("5a91486c973deea304e26138206723278d9d269f4fe03bfc9e5fdb93e927236f"))
                .timestamp(new Date(timestamp.getTime() + 2000))
                .state(new Digest("99a6cae10c5ab5b528e968378ead4bc8ef56a6613227e85e28845d9e398103ae"))
                .deployCount(1)
                .transferCount(2)
                .eraId(3)
                .proposer(proposer)
                .blockHeight(4L)
                .eventId(1L)
                .build()
        );

        blockRepository.save(Block.builder()
                .blockHash(new Digest("5a91486c973deea304e26138206723278d9d269f4fe03bfc9e5fdb93e9272371"))
                .parentHash(new Digest("5a91486c973deea304e26138206723278d9d269f4fe03bfc9e5fdb93e9272370"))
                .timestamp(new Date(timestamp.getTime() + 3000))
                .state(new Digest("99a6cae10c5ab5b528e968378ead4bc8ef56a6613227e85e28845d9e398103af"))
                .deployCount(2)
                .transferCount(3)
                .eraId(3)
                .proposer(proposer)
                .blockHeight(5L)
                .eventId(2L)
                .build()
        );

        blockRepository.save(Block.builder()
                .blockHash(new Digest("5a91486c973deea304e26138206723278d9d269f4fe03bfc9e5fdb93e9272372"))
                .parentHash(new Digest("5a91486c973deea304e26138206723278d9d269f4fe03bfc9e5fdb93e9272372"))
                .timestamp(new Date(timestamp.getTime() + 4000))
                .state(new Digest("99a6cae10c5ab5b528e968378ead4bc8ef56a6613227e85e28845d9e398103af"))
                .deployCount(2)
                .transferCount(3)
                .eraId(3)
                .proposer(PublicKey.fromTaggedHexString("017d96b9a63abcb61c870a4f55187a0a7ac24096bdb5fc585c12a686a4d892009f"))
                .blockHeight(5L)
                .eventId(2L)
                .build()
        );

        assertThat(blockRepository.count(), is(5L));

        Page<Block> byProposer = blockRepository.findByProposer(proposer, PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, "timestamp")));
        assertThat(byProposer.getNumber(), is(0));
        assertThat(byProposer.getTotalElements(), is(4L));
        assertThat(byProposer.getTotalPages(), is(2));

        assertThat(byProposer.getContent().get(0).getProposer(), is(proposer));
        assertThat(byProposer.getContent().get(0).getTimestamp().getTime(), is(timestamp.getTime()));

        assertThat(byProposer.getContent().get(1).getProposer(), is(proposer));
        assertThat(byProposer.getContent().get(1).getTimestamp().getTime(), is(new Date(timestamp.getTime() + 1000).getTime()));

        // Reverse sort order
        byProposer = blockRepository.findByProposer(proposer, PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "timestamp")));

        assertThat(byProposer.getContent().get(0).getProposer(), is(proposer));
        assertThat(byProposer.getContent().get(0).getTimestamp().getTime(), is(new Date(timestamp.getTime() + 3000).getTime()));

        assertThat(byProposer.getContent().get(1).getProposer(), is(proposer));
        assertThat(byProposer.getContent().get(1).getTimestamp().getTime(), is(new Date(timestamp.getTime() + 2000).getTime()));
    }
}
