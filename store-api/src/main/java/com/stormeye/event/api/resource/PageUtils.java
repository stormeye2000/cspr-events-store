package com.stormeye.event.api.resource;

import org.springframework.data.domain.Sort;

/**
 * @author ian@meywood.com
 */
public class PageUtils {

    /** The timestamp filename used for default sorting */
    public static final String TIMESTAMP = "timestamp";

    static Sort getSort(String orderBy, Sort.Direction orderDirection) {
        if (TIMESTAMP.equals(orderBy)) {
            return Sort.by(orderDirection, orderBy);
        } else return Sort.by(
                new Sort.Order(orderDirection, orderBy),
                new Sort.Order(Sort.Direction.ASC, TIMESTAMP)
        );
    }
}
