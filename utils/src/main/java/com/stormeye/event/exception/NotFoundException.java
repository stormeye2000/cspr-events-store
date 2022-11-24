package com.stormeye.event.exception;

/**
 * Exception that is thrown when a request event is not found.
 *
 * @author ian@meywood.com
 */
public class NotFoundException extends RuntimeException {

    public NotFoundException(final String message) {
        super(message);
    }
}
