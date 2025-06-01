package com.apple.team_prometheus.global.config.task

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.TaskExecutor
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor

@Configuration
class AsyncConfig {

    @Bean
    fun taskExecutor(): TaskExecutor {
        return ThreadPoolTaskExecutor().apply {
            corePoolSize = 10
            maxPoolSize = 20
            setQueueCapacity(100)
            setThreadNamePrefix("fcm-async-")
            initialize()
        }
    }
}
