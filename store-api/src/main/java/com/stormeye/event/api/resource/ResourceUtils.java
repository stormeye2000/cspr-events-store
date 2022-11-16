package com.stormeye.event.api.resource;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    public static Pageable buildPageRequest(final int page,
                                            final int size,
                                            final Enum<?> orderBy,
                                            final Sort.Direction orderDirection) {
        return PageRequest.of(page - 1, size, ResourceUtils.getSort(orderBy, orderDirection));
    }

    private static Sort getSort(final Enum<?> orderBy, final Sort.Direction orderDirection) {
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
