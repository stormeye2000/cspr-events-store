package com.stormeye.event.audit.execption;

/**
 * Default runtime exception for the application
 *
 * @author ian@meywood.com
 */
public class AuditServiceException extends RuntimeException {

    public AuditServiceException(final Throwable throwable) {
        super(throwable);
    }

    public AuditServiceException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
