package com.apple.team_prometheus.global.config.fcm

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.FileInputStream


@Configuration
class FCMConfig {

    @Value("\${fcm.key-path}")
    lateinit var keyPath: String

    @Bean
    fun firebaseMessaging(): FirebaseMessaging {
        try {
            // 서비스 계정 키 파일 로드
            val serviceAccount = FileInputStream(keyPath)
            println("Using Firebase service account key: $keyPath")

            // FirebaseOptions 설정
            val options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build()

            // FirebaseApp이 이미 초기화되지 않았는지 확인
            val firebaseApp = if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options)
            } else {
                FirebaseApp.getInstance()
            }

            // FirebaseMessaging 인스턴스 반환
            return FirebaseMessaging.getInstance(firebaseApp)
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException("Failed to initialize Firebase Messaging", e)
        }
    }
}