package com.cosain.trilo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {
    private static int CORE_POOL_SIZE = 15; // 동시에 실행시킬 쓰레드의 개수 ( default : 1 )
    private static int MAX_POOL_SIZE = 25; // 쓰레드 풀의 최대 사이즈를 지정 ( default : Integer.MAX_VALUE )
    private static int QUEUE_CAPACITY = 10; // 큐의 사이즈 ( default : Integer.MAX_VALUE )
    private static String THREAD_NAME_PREFIX = "async-task"; // Thread name prefix

    @Bean(name = "threadPoolTaskExecutor")
    public Executor asyncTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(CORE_POOL_SIZE);
        executor.setMaxPoolSize(MAX_POOL_SIZE);
        executor.setQueueCapacity(QUEUE_CAPACITY);
        executor.setThreadNamePrefix(THREAD_NAME_PREFIX);
        executor.initialize();
        return executor;
    }
}
