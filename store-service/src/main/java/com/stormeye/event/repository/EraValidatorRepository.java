package com.stormeye.event.repository;

import com.stormeye.event.service.storage.domain.EraValidator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author ian@meywood.com
 */
public interface EraValidatorRepository extends PagingAndSortingRepository<EraValidator, Long>, JpaRepository<EraValidator, Long> {
}