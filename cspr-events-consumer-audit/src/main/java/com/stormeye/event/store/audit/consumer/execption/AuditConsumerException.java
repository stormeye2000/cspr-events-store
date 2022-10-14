package com.stormeye.event.store.audit.consumer.execption;

/**
 * Default runtime exception for the application
 *
 * @author ian@meywood.com
 */
public class AuditConsumerException extends RuntimeException {

    public AuditConsumerException(final Throwable throwable) {
        super(throwable);
    }

    public AuditConsumerException(String message, Throwable cause) {
        super(message, cause);
    }
}
