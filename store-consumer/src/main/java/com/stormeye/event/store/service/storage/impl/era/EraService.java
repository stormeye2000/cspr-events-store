package com.stormeye.event.store.service.storage.impl.era;

import com.stormeye.event.repository.EraRepository;
import com.stormeye.event.service.storage.domain.Era;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;

/**
 * The service for storing and retrieving Era
 *
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

    public Optional<Era> findById(long id) {
        return eraRepository.findById(id);
    }

    public Era create(final long eraId, final long height, final Date timeStamp, final String protocolVersion) {
        return this.store(
                Era.builder()
                        .id(eraId)
                        .endBlockHeight(height)
                        .endTimestamp(timeStamp)
                        .protocolVersion(protocolVersion)
                        .build()
        );
    }
}
