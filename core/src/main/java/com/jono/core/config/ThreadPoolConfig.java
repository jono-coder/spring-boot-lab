package com.jono.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class ThreadPoolConfig {

    @Bean(name = "taskExecutor")
    public Executor cpuTaskExecutor() {
        final var executor = new ThreadPoolTaskExecutor();
        final var corePoolSize = Runtime.getRuntime().availableProcessors(); // Match CPU cores
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(corePoolSize); // Fixed size for CPU tasks
        executor.setQueueCapacity(100); // Queue for pending tasks
        executor.setThreadNamePrefix("cpu-task-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy()); // Fallback to caller
        executor.initialize();
        return executor;
    }

    // Custom wrapper for I/O executor to apply Semaphore (uses virtual threads)
    @Bean(name = "ioTaskExecutor")
    public Executor ioTaskExecutor(final Semaphore ioSemaphore) {
        return new SemaphoreGuardedExecutor(Executors.newVirtualThreadPerTaskExecutor(), ioSemaphore);
    }

    // Semaphore for I/O back pressure (e.g., 20 permits to match DB connection pool)
    @Bean
    public Semaphore ioSemaphore() {
        return new Semaphore(20, true); // Fair Semaphore for FIFO order
    }

}
