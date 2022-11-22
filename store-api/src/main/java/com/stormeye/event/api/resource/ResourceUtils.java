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

    private ResourceUtils() {
        // Prevent construction
    }

    static Pageable buildPageRequest(final int page,
                                     final int size,
                                     final Enum<?> orderBy,
                                     final Sort.Direction orderDirection,
                                     final Enum<?> defaultSort) {
        return PageRequest.of(page - 1, size, getSortWithDefault(orderBy, orderDirection, defaultSort));
    }

    static BigInteger zeroIfNull(final BigInteger bigInteger) {
        return bigInteger == null ? BigInteger.ZERO : bigInteger;
    }

    private static Sort getSortWithDefault(final Enum<?> orderBy,
                                           final Sort.Direction orderDirection,
                                           final Enum<?> defaultSort) {
        if (defaultSort.equals(orderBy)) {
            return Sort.by(orderDirection, orderBy.name());
        } else return Sort.by(
                new Sort.Order(orderDirection, orderBy.name()),
                new Sort.Order(Sort.Direction.ASC, defaultSort.name())
        );
    }
}
