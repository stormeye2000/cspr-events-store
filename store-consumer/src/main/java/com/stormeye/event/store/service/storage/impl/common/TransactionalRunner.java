package com.stormeye.event.store.service.storage.impl.common;

import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.concurrent.Callable;

/**
 * A service that executed a callable function withing a JPA transaction.
 *
 * @author ian@meywood.com
 */
@Component
public class TransactionalRunner {

    /**
     * Executes a callable within a JPA transaction.
     *
     * @param callable the callable to execute within a transaction
     * @param <T>      the type of the value being returned by the callable
     * @return the result of the callable's execution
     * @throws Exception on an error
     */
    @Transactional
    public <T> T runInTransaction(final Callable<T> callable) throws Exception {
        return callable.call();
    }
}
