package com.stormeye.event.api.resource;

import com.stormeye.event.api.exception.ApiBadRequestException;
import com.stormeye.event.api.exception.ApiNotFoundException;
import com.stormeye.event.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author ian@meywood.com
 */
@RestControllerAdvice
public class RestResponseEntityExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ResponseBody
    public String handleNotFound(final NotFoundException e) {
        return e.getMessage();
    }

    @ExceptionHandler(ApiNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ResponseBody
    public String handleApiNotFound(final ApiNotFoundException e) {
        return e.getMessage();
    }

    @ExceptionHandler(ApiBadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public String handleNotFound(final ApiBadRequestException e) {
        return e.getMessage();
    }

}
