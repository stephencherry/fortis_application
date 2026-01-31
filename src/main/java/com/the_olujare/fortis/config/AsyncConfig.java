package com.the_olujare.fortis.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Configuration for asynchronous task execution.
 *
 * Enables @Async annotation support for background processing.
 * Primary use case: sending emails without blocking HTTP responses.
 *
 * Thread pool configuration:
 * - Core pool size: 2 threads (sufficient for email sending)
 * - Max pool size: 5 threads (for bursts)
 * - Queue capacity: 100 (pending tasks)
 * - Thread name prefix: "async-email-"
 *
 * This prevents email delays from affecting API response times.
 */

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {
    public Executor getAsyncExecutor(){
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(2);
        threadPoolTaskExecutor.setMaxPoolSize(5);
        threadPoolTaskExecutor.setQueueCapacity(100);
        threadPoolTaskExecutor.setThreadNamePrefix("async-email-");
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }
}