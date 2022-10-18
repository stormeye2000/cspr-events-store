package com.stormeye.event.store.services.storage.era.repository;

import com.stormeye.event.store.services.storage.era.domain.Era;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author ian@meywood.com
 */
public interface EraRepository extends PagingAndSortingRepository<Era, Long>, JpaRepository<Era, Long> {

}
