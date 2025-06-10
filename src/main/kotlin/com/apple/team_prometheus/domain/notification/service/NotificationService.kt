package com.apple.team_prometheus.domain.notification.service

import com.apple.team_prometheus.domain.notification.dto.NotificationDto
import com.apple.team_prometheus.domain.notification.entity.Notification
import com.apple.team_prometheus.domain.notification.repository.FCMTokenRepository
import com.apple.team_prometheus.domain.notification.repository.NotificationRepository
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingException
import com.google.firebase.messaging.Message
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.task.TaskExecutor
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class NotificationService(
    private val notificationRepository: NotificationRepository,
    private val firebaseMessaging: FirebaseMessaging,
    @Qualifier("taskExecutor") private val taskExecutor: TaskExecutor,
    private val fcmTokenRepository: FCMTokenRepository
) {

    fun createNotification(request: NotificationDto.Request): NotificationDto.Response {
        val notification = Notification(
            title = request.title,
            detail = request.detail,
            author = "System",
            dueDate = LocalDate.now()
        ).let(notificationRepository::save)

        // 비동기로 푸시 알림 전송
        taskExecutor.execute {
            fcmTokenRepository.findAll() // 모든 FCM 토큰 조회
                .map { it.token }
                .forEach { token ->
                    try {
                        sendPushMessage(token, request)
                    } catch (e: FirebaseMessagingException) {
                        when (e.errorCode.toString()) {
                            "invalid-argument" -> {
                                fcmTokenRepository.deleteByToken(token)
                            }
                            "invalid-registration-token" -> {
                                fcmTokenRepository.deleteByToken(token)
                            }
                            else -> {
                                println("푸시 전송 실패 (토큰: $token): ${e.message}")
                            }
                        }
                    } catch (e: Exception) {
                        println("알 수 없는 오류 (토큰: $token): ${e.message}")
                    }
                }
        }

        return NotificationDto.Response(
            id = notification.id,
            title = notification.title,
            detail = notification.detail,
            author = notification.author,
            dueDate = notification.dueDate
        )
    }

    @Throws(FirebaseMessagingException::class)
    fun sendPushMessage(targetToken: String, request: NotificationDto.Request) {
        val message = Message.builder()
            .setToken(targetToken)
            .setNotification(
                com.google.firebase.messaging.Notification.builder()
                    .setTitle(request.title)
                    .setBody(request.detail)
                    .build()
            )
            .build()

        firebaseMessaging.send(message)
    }

    fun updateNotification(
        id: Long,
        request: NotificationDto.Request
    ): NotificationDto.Response {

        val notification = notificationRepository.findById(id)
            .orElseThrow {
                IllegalArgumentException("Notification not found with id: $id")
            }

        notification.title = request.title
        notification.detail = request.detail
        notification.dueDate = LocalDate.now()

        val updatedNotification = notificationRepository.save(notification)

        return NotificationDto.Response(
            id = updatedNotification.id,
            title = updatedNotification.title,
            detail = updatedNotification.detail,
            author = updatedNotification.author,
            dueDate = updatedNotification.dueDate
        )
    }

    fun getAllNotifications(pageable: Pageable): NotificationDto.ResponseWithPage {
        val notifications :Page<Notification> = notificationRepository.findAll(pageable)

        return NotificationDto.ResponseWithPage(
            content = notifications.content.map {
                NotificationDto.Response(
                    id = it.id,
                    title = it.title,
                    detail = it.detail,
                    author = it.author,
                    dueDate = it.dueDate
                )
            },
            totalPages = notifications.totalPages,
            size = notifications.size
        )
    }

    fun deleteNotification(id: Long) {

        val notification = notificationRepository.findById(id)
            .orElseThrow {
                IllegalArgumentException("해당 공지 찾을 수 없음: $id")
            }

        notificationRepository.delete(notification)

    }

}
