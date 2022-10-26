package com.stormeye.event.exception;

/**
 * @author ian@meywood.com
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
