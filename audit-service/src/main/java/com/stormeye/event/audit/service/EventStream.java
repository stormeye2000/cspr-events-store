package com.stormeye.event.audit.service;

import java.io.IOException;
import java.io.InputStream;

/**
 * An input stream for a JSON event that also contains the size of the event in bytes
 *
 * @author ian@meywood.com
 */
public class EventStream extends InputStream {

    /** The steam being delegated to */
    private final InputStream inputStream;
    /** The size of the data being read from the stream */
    private final long size;

    public EventStream(final InputStream inputStream, final long size) {
        this.inputStream = inputStream;
        this.size = size;
    }

    @Override
    public int read() throws IOException {
        return inputStream.read();
    }

    public long getSize() {
        return size;
    }
}
