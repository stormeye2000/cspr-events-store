package com.stormeye.event.api.exception;

public class ApiBadRequestException extends RuntimeException {

    public ApiBadRequestException(final String message) {
        super(message);
    }

    public ApiBadRequestException() {
        super();
    }
}
