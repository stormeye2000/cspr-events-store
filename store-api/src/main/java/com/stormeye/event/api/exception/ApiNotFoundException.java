package com.stormeye.event.api.exception;

public class ApiNotFoundException extends RuntimeException {

    public ApiNotFoundException() {
        super("Resource does not exist");
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

}
