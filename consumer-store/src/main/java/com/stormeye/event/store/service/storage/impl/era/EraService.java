package com.stormeye.event.store.service.storage.impl.era;

import com.stormeye.event.service.storage.domain.Era;
import com.stormeye.event.repository.EraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author ian@meywood.com
 */
@Component
public class EraService {

    private final EraRepository eraRepository;

    @Autowired
    public EraService(final EraRepository eraRepository) {
        this.eraRepository = eraRepository;
    }

    public Era store(final Era era) {
        return eraRepository.save(era);
    }
}
