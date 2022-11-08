package com.stormeye.event.store.service.storage.impl.era;

import com.casper.sdk.model.key.PublicKey;
import com.stormeye.event.repository.EraValidatorRepository;
import com.stormeye.event.service.storage.domain.EraValidator;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

/**
 * @author ian@meywood.com
 */
@Service
public class EraValidatorService {

    private final EraValidatorRepository eraValidatorRepository;

    public EraValidatorService(EraValidatorRepository eraValidatorRepository) {
        this.eraValidatorRepository = eraValidatorRepository;
    }


    public void create(long eraId, PublicKey validator, BigInteger weight, int rewards, int hasEquivocation, int  wasActive) {
       this.eraValidatorRepository.save(new EraValidator(eraId, validator, weight, rewards, hasEquivocation, wasActive));
    }
}
