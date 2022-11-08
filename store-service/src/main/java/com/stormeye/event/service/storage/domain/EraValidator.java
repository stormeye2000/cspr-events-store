package com.stormeye.event.service.storage.domain;

import com.casper.sdk.model.key.PublicKey;
import com.stormeye.event.service.conveter.PublicKeyConverter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigInteger;

/**
 * @author ian@meywood.com
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
public class EraValidator {
    @Id
    private long eraId;
    @Convert(converter = PublicKeyConverter.class)
    private PublicKey validator;
    private BigInteger weight;
    private int rewards;
    private int hasEquivocation;
    private int wasActive;
}
