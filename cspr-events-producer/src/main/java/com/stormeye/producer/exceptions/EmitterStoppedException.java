package com.stormeye.producer.exceptions;

/**
 * Emitter stopped mid stream
 */
public class EmitterStoppedException extends RuntimeException{

    public EmitterStoppedException(final String message){
        super(message);
    }
}
