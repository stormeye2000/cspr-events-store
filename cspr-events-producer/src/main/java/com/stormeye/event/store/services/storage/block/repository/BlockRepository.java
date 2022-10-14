package com.stormeye.event.store.services.storage.block.repository;

import com.stormeye.event.store.services.storage.block.domain.Block;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * The Spring Data JPA repository for {@link Block} domain objects
 *
 * @author ian@meywood.com
 */
public interface BlockRepository extends PagingAndSortingRepository<Block, Long>, JpaRepository<Block, Long> {

}
