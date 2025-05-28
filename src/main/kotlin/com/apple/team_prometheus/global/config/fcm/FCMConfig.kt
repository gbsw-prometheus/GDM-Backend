package com.apple.team_prometheus.global.config.fcm

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Configuration
import java.io.FileInputStream


@Configuration
class FCMConfig {

    @PostConstruct
    fun initialize() {
        try {
            val serviceAccount: FileInputStream = FileInputStream("../../../firebaseKey/team-prometheus-68425-firebase-adminsdk-fbsvc-2c3d30c54a.json")

            val firebaseOption: FirebaseOptions = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build()

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(firebaseOption)
            } else {
                FirebaseApp.getInstance()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException("Failed to initialize Firebase App", e)
        }
    }
}