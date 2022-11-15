package com.stormeye.event.repository;

import com.casper.sdk.model.common.Digest;
import com.casper.sdk.model.key.PublicKey;
import com.stormeye.event.service.storage.domain.Block;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

/**
 * The Spring Data JPA repository for {@link Block} domain objects
 *
 * @author ian@meywood.com
 */
public interface BlockRepository extends PagingAndSortingRepository<Block, Long>, JpaRepository<Block, Long> {

    /**
     * Finds a block by its block hash
     *
     * @param blockHash the block hash of the block to find
     * @return an optional containing the block if found
     */
    Optional<Block> findByBlockHash(final Digest blockHash);

    /**
     * Finds a block by its block hash and eventId
     *
     * @param blockHash the block hash of the block to find
     * @param eventId   the eventId of the block
     * @return an optional containing the block if found
     */
    Block findByBlockHashAndEventId(final Digest blockHash, final long eventId);

    /**
     * Finds a page of blocks proposed by the proposer
     *
     * @param proposer the public key of the proposer
     * @param request  the page request
     * @return a page of blocks by proposer
     */
    Page<Block> findByProposer(final PublicKey proposer, final Pageable request);
}
