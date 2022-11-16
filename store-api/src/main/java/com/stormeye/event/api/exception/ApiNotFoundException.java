package com.stormeye.event.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ApiNotFoundException extends RuntimeException {

    public ApiNotFoundException() {
        super("Resource does not exist");
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

}
