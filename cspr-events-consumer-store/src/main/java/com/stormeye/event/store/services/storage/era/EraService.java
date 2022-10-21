package com.stormeye.event.store.services.storage.era;

import com.casper.sdk.model.block.JsonBlockHeader;
import com.casper.sdk.model.event.blockadded.BlockAdded;
import com.stormeye.event.store.services.storage.StorageFactory;
import com.stormeye.event.store.services.storage.StorageService;
import com.stormeye.event.store.services.storage.era.domain.Era;
import com.stormeye.event.store.services.storage.era.repository.EraRepository;
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
