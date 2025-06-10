package com.apple.team_prometheus.domain.notification.controller

import com.apple.team_prometheus.domain.auth.entity.AuthUser
import com.apple.team_prometheus.domain.notification.dto.FCM
import com.apple.team_prometheus.domain.notification.service.NotificationService
import com.apple.team_prometheus.domain.notification.dto.NotificationDto
import com.apple.team_prometheus.domain.notification.service.FCMTokenService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping(value = ["/api/notifications"])
class NotificationController(
    private val notificationService: NotificationService,
    private val fcmTokenService: FCMTokenService
) {

    @PostMapping(value = ["/fcm-register"])
    fun registerFCMToken(
        @Valid @RequestBody request: FCM.Request,
        authentication: Authentication
    ): ResponseEntity<FCM.Response> {

        val user = authentication.principal as AuthUser

        fcmTokenService.saveOrUpdateToken(user, request)

        return ResponseEntity.ok(
            FCM.Response("FCM token registered successfully")
        )
    }

    @PostMapping(value = ["/create"])
    fun createNotifications(
        @Valid @RequestBody request: NotificationDto.Request
    ): ResponseEntity<NotificationDto.Response> {

        val response = notificationService.createNotification(request)

        val targetTokens = fcmTokenService.getAllFCMTokens()

        if (targetTokens.isEmpty()) {
            return ResponseEntity.ok(response)
        }

        targetTokens.forEach { token: String ->
            try {
                notificationService.sendPushMessage(token, request)
            } catch (e: Exception) {
                println("푸시 실패 (토큰: $token): ${e.message}")
            }
        }

        return ResponseEntity.ok(response)
    }
}
