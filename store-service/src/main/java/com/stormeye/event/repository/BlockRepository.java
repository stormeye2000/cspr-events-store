package com.stormeye.event.repository;

import com.casper.sdk.model.common.Digest;
import com.stormeye.event.service.storage.domain.Block;
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
}
