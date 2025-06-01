package com.apple.team_prometheus.domain.notification.service

import com.apple.team_prometheus.domain.auth.entity.AuthUser
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
    fun saveToken(user: AuthUser, token: String, deviceInfo: String) {

        val existingToken = fcmTokenRepository.findByUserAndToken(user, token)

        if (existingToken == null) {

            val newToken = FCMToken(
                user = user,
                token = token,
                deviceInfo = deviceInfo,
                createDate = LocalDateTime.now()
            )

            fcmTokenRepository.save(newToken)
        } else {

            existingToken.token = token

            fcmTokenRepository.save(existingToken)
        }
    }
}