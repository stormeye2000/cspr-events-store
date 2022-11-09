package com.stormeye.event.store.service.storage.impl.era;

import com.casper.sdk.model.key.PublicKey;
import com.stormeye.event.repository.EraValidatorRepository;
import com.stormeye.event.service.storage.domain.EraValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Optional;

/**
 * The service for storing and retrieving {@link EraValidator} objects.
 *
 * @author ian@meywood.com
 */
@Service
public class EraValidatorService {

    private final EraValidatorRepository eraValidatorRepository;

    public EraValidatorService(EraValidatorRepository eraValidatorRepository) {
        this.eraValidatorRepository = eraValidatorRepository;
    }


    public EraValidator create(final long eraId,
                               final PublicKey validator,
                               final BigInteger weight,
                               final BigInteger rewards,
                               final int hasEquivocation,
                               final int wasActive) {
        return this.eraValidatorRepository.save(
                new EraValidator(eraId, validator, weight, rewards, hasEquivocation, wasActive)
        );
    }


    public Page<EraValidator> findByEraId(long eraId, final Pageable pageable) {
        return this.eraValidatorRepository.findByEraId(eraId, pageable);
    }

    public Optional<EraValidator> findByEraIdAndPublicKey(long eraId, PublicKey validator) {
        return this.eraValidatorRepository.findByEraIdAndPublicKey(eraId, validator);
    }
}
