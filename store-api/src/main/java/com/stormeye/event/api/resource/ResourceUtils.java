package com.stormeye.event.api.resource;

import org.springframework.data.domain.Sort;

import java.math.BigInteger;

/**
 * REST API utility methods.
 *
 * @author ian@meywood.com
 */
class ResourceUtils {

    /** The timestamp filename used for default sorting */
    public static final String TIMESTAMP = "timestamp";

    static Sort getSort(final Enum<?> orderBy, Sort.Direction orderDirection) {
        if (TIMESTAMP.equals(orderBy.name())) {
            return Sort.by(orderDirection, orderBy.name());
        } else return Sort.by(
                new Sort.Order(orderDirection, orderBy.name()),
                new Sort.Order(Sort.Direction.ASC, TIMESTAMP)
        );
    }

    static BigInteger zeroIfNull(final BigInteger bigInteger) {
        return bigInteger == null ? BigInteger.ZERO : bigInteger;
    }
}
