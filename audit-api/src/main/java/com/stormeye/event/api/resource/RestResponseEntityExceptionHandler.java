package com.stormeye.event.api.resource;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * Error/Exception Advice
 *
 * @author ian@meywood.com
 */
@RestControllerAdvice
public class RestResponseEntityExceptionHandler {

    public static final String INVALID_PATH_MSG = "invalid path: expected '/events/main', '/events/deploys' or '/events/sigs'";

    /**
     * When not handler is found for a URL this method provides a 404 status with an error message that matches the
     * casper nodes message.
     * <p>
     * This requires that the {@link org.springframework.web.servlet.DispatcherServlet#setThrowExceptionIfNoHandlerFound(boolean)}
     * is set to true
     *
     * @return the response body error message
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ResponseBody
    public String handleNotFound() {
        return INVALID_PATH_MSG;
    }
}
