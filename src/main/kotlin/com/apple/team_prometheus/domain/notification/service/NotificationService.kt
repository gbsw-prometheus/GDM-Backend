package com.apple.team_prometheus.domain.notification.service

import com.apple.team_prometheus.domain.auth.repository.AuthRepository
import com.apple.team_prometheus.domain.notification.dto.NotificationDto
import com.apple.team_prometheus.domain.notification.entity.Notification
import com.apple.team_prometheus.domain.notification.repository.NotificationRepository
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingException
import com.google.firebase.messaging.Message
import org.springframework.stereotype.Service


@Service
class NotificationService(
    private val notificationRepository: NotificationRepository,
    private val authRepository: AuthRepository
) {

    fun createNotification(request: NotificationDto.Request): NotificationDto.Response {

        val notification: Notification = Notification(
            title = request.title,
            detail = request.detail,
            author = "System",
            dueDate = java.time.LocalDate.now()
        )

        val saved = notificationRepository.save(notification)

        val userTokens: List<String> = authRepository.findAllByFcmTokenIsNotNull()
            .mapNotNull {
                it.fcmToken
            }

        userTokens.forEach { token ->
            try{
                sendPushMessage(token, request)
            } catch (e: Exception) {
                // Log the error or handle it as needed
                println("Failed to send notification to token $token: ${e.message}")
            }
        }

        return NotificationDto.Response(
            id = saved.id,
            title = saved.title,
            detail = saved.detail,
            dueDate = saved.dueDate,
            author = saved.author
        )
    }

    @Throws(FirebaseMessagingException::class)
    fun sendPushMessage(targetToken: String?, request: NotificationDto.Request) {

        try {
            val message: Message = Message.builder()
                .setToken(targetToken)
                .setNotification(
                    com.google.firebase.messaging.Notification.builder()
                        .setTitle(request.title)
                        .setBody(request.detail)
                        .build()
                )
                .build()
            FirebaseMessaging.getInstance().send(message)
        } catch (e: FirebaseMessagingException) {
            e.printStackTrace()
            throw e
        }
    }

}