package com.magese.music.config;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池配置
 *
 * @author Magese
 * @since 2023/4/17 14:26
 */
@Slf4j
@EnableAsync
@Configuration
public class ThreadPoolConfig {

    private ThreadPoolTaskExecutor cmdExecutor;

    @Bean(value = "cmdExecutor")
    public ThreadPoolTaskExecutor cmdExecutor() {
        log.info("【SpringBean注册】 => org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor");
        this.cmdExecutor = new ThreadPoolTaskExecutor();
        cmdExecutor.setCorePoolSize(20);
        cmdExecutor.setMaxPoolSize(20);
        cmdExecutor.setQueueCapacity(Integer.MAX_VALUE);
        cmdExecutor.setKeepAliveSeconds(60);
        cmdExecutor.setThreadNamePrefix("cmd-task-");
        cmdExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        cmdExecutor.initialize();
        return cmdExecutor;
    }

    @PreDestroy
    public void destroy() {
        cmdExecutor.shutdown();
    }
}
