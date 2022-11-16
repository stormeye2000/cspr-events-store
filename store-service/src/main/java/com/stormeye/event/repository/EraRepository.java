package com.stormeye.event.repository;

import com.stormeye.event.service.storage.domain.Era;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Spring Data Repository for {@link Era} domain objects
 *
 * @author ian@meywood.com
 */
public interface EraRepository extends PagingAndSortingRepository<Era, Long>, JpaRepository<Era, Long> {

}
