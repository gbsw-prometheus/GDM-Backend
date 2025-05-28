package com.apple.team_prometheus.domain.notification.service

import com.apple.team_prometheus.domain.notification.dto.NotificationDto
import com.apple.team_prometheus.domain.notification.entity.Notification
import com.apple.team_prometheus.domain.notification.repository.NotificationRepository
import org.springframework.stereotype.Service


@Service
class NotificationService(
    private val notificationRepository: NotificationRepository
) {

    fun createNotification(request: NotificationDto.Request): NotificationDto.Response {

        val notification: Notification = Notification(
            title = request.title,
            detail = request.detail,
            author = "System",
            dueDate = java.time.LocalDate.now()
        )

        val saved = notificationRepository.save(notification)

        return NotificationDto.Response(
            id = saved.id,
            title = saved.title,
            detail = saved.detail,
            dueDate = saved.dueDate,
            author = saved.author
        )
    }

}