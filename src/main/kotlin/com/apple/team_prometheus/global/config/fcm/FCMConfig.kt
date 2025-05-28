package com.apple.team_prometheus.global.config.fcm

import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Configuration
import java.io.FileInputStream


@Configuration
class FCMConfig {

    @PostConstruct
    fun initialize() {
        val serviceAccount: FileInputStream = FileInputStream("src")

    }
}