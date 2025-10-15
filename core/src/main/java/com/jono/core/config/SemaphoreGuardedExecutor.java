package com.jono.core.config;

import org.slf4j.Logger;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;

import static org.slf4j.LoggerFactory.getLogger;

public class SemaphoreGuardedExecutor implements Executor {

    private static final Logger LOGGER = getLogger(SemaphoreGuardedExecutor.class);

    private final Executor delegate;
    private final Semaphore semaphore;

    public SemaphoreGuardedExecutor(final Executor delegate, final Semaphore semaphore) {
        this.delegate = delegate;
        this.semaphore = semaphore;
    }

    @Override
    public void execute(final Runnable command) {
        Objects.requireNonNull(command);

        try {
            semaphore.acquire(); // Block if no permits (back pressure)

            LOGGER.info("SemaphoreGuardedExecutor executing...");

            delegate.execute(() -> {
                try {
                    command.run();
                } finally {
                    semaphore.release(); // Always release
                }
            });
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RejectedExecutionException(e);
        }
    }

}
