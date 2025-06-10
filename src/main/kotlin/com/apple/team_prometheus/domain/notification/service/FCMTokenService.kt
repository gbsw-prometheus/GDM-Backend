package com.apple.team_prometheus.domain.notification.service

import com.apple.team_prometheus.domain.auth.entity.AuthUser
import com.apple.team_prometheus.domain.notification.dto.FCM
import com.apple.team_prometheus.domain.notification.entity.FCMToken
import com.apple.team_prometheus.domain.notification.repository.FCMTokenRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime


@Service
class FCMTokenService(
    private val fcmTokenRepository: FCMTokenRepository
) {

    @Transactional
    fun saveOrUpdateToken(user: AuthUser, fcm: FCM.Request) {

        val existingToken = fcmTokenRepository.findByUserAndToken(user, fcm.token)

        if (existingToken == null) {

            val newToken = FCMToken(
                user = user,
                token = fcm.token,
                deviceInfo = fcm.deviceInfo ?: "Unknown Device",
                createDate = LocalDateTime.now()
            )

            fcmTokenRepository.save(newToken)
        } else {

            existingToken.token = fcm.token

            fcmTokenRepository.save(existingToken)
        }
    }

    fun getAllFCMTokens(): List<String> {
        return fcmTokenRepository.findAll()
            .map {
                it.token
            }
    }

}