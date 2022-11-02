package com.stormeye.event.audit.service;

import com.casper.sdk.model.event.EventType;
import com.stormeye.event.audit.execption.MaxEventsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * The context of an event replay. Used to maintain state when replaying events
 *
 * @author ian@meywood.com
 */
class EventReplayContext implements Iterator<EventInfo> {

    /** The current page of events obtained from the database */
    private Page<EventInfo> currentPage;
    /** The ID of the most recent event provided by the context */
    private long currentId;
    /** The Iterator for the currentPage */
    private Iterator<EventInfo> iterator;
    /** The maximum requested events, includes empty events sent as colons */
    private final long maxEvents;
    private final Map<String, String> queryMap;
    /** The service the events are obtained from */
    private final EventAuditService eventAuditService;
    /** The type of event requested */
    private final EventType eventType;
    /** The logger */
    private final Logger logger = LoggerFactory.getLogger(EventReplayContext.class);
    /** The number of events provided by the context */
    private long count;
    /** Flag that indicates if the initial version has been sent */
    private boolean versionSent = false;
    /** The current version of the most recent event sent */
    private String currentVersion = "1.0.0";

    public EventReplayContext(final EventType eventType,
                              final long currentId,
                              final long maxEvents,
                              final Map<String, String> queryMap,
                              final EventAuditService eventAuditService) {
        this.eventType = eventType;
        this.currentId = currentId;
        this.maxEvents = maxEvents;
        this.queryMap = queryMap;
        this.eventAuditService = eventAuditService;

        getNextPage();
    }

    private void getNextPage() {
        this.currentPage = eventAuditService.findAllSince(currentId, eventType, queryMap, Pageable.ofSize(10));
        this.iterator = this.currentPage.iterator();
    }

    @Override
    public boolean hasNext() {

        final boolean hasNext;

        if (this.iterator.hasNext()) {
            hasNext = true;
        } else if (this.currentPage.isLast()) {
            hasNext = false;
        } else if (this.currentPage.hasNext()) {
            getNextPage();
            hasNext = this.iterator.hasNext();
        } else {
            hasNext = false;
        }

        logger.debug("hasNext() = {}", hasNext);

        return hasNext;
    }

    public boolean isVersionSent() {
        return versionSent;
    }

    public void setVersionSent(boolean versionSent) {
        this.versionSent = versionSent;
    }

    public String getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(final String currentVersion) {
        this.currentVersion = currentVersion;
    }

    @Override
    public EventInfo next() {

        if (hasNext()) {
            return getNextEventInfo();
        } else {
            throw new NoSuchElementException();
        }
    }

    private EventInfo getNextEventInfo() {

        var next = this.iterator.next();

        refreshCurrentId(next);

        return next;
    }

    private void refreshCurrentId(final EventInfo next) {
        var id = (Number) next.getEventId();
        if (id != null) {
            this.currentId = id.longValue();
        }
    }

    public void throwIfMaxEventsExceeded() {
        if (maxEvents > 0 && count > maxEvents) {
            throw new MaxEventsException();
        }
        count++;
    }

    public boolean isDifferentEventVersion(final EventInfo eventInfo) {
        return !isVersionSent() || !getCurrentVersion().equals(eventInfo.getVersion());
    }
}
