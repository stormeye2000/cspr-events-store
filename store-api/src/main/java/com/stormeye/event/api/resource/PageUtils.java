package com.stormeye.event.api.resource;

import org.springframework.data.domain.Sort;

/**
 * @author ian@meywood.com
 */
public class PageUtils {

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
}
