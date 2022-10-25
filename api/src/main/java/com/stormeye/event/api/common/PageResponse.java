package com.stormeye.event.api.common;

import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

/**
 * The JSON for a page of data that matched the current event store API
 *
 * @author ian@meywood.com
 */
public class PageResponse<T> {
    private final List<T> data;

    private final long pageCount;
    private final long itemCount;
    private final int pageNumber;

    @SuppressWarnings("unused")
    public PageResponse() {
        this(new ArrayList<>(), 0, 0, 0);
    }

    public PageResponse(final List<T> data, final long pageCount, final long itemCount, final int pageNumber) {
        this.data = data;
        this.pageCount = pageCount;
        this.itemCount = itemCount;
        this.pageNumber = pageNumber + 1;
    }

    public PageResponse(final Page<T> page) {
        this(page.getContent(), page.getTotalPages(), page.getTotalElements(), page.getNumber());
    }

    public List<T> getData() {
        return data;
    }

    public long getPageCount() {
        return pageCount;
    }

    public long getItemCount() {
        return itemCount;
    }

    public int getPageNumber() {
        return pageNumber;
    }
}
