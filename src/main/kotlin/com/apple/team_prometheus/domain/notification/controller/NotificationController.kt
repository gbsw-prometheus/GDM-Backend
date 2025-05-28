package com.apple.team_prometheus.domain.notification.controller

import com.apple.team_prometheus.domain.notification.dto.NotificationDto
import com.apple.team_prometheus.domain.notification.service.NotificationService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping(value = ["/api/notifications"])
class NotificationController(
    private val notificationService: NotificationService
) {

    @PostMapping(value = ["/send"])
    fun createNotifications(
        @Valid @RequestBody request: NotificationDto.Request
    ): ResponseEntity<NotificationDto.Response> {

        val response = notificationService.createNotification(request)

        return ResponseEntity.ok(response)

    }
}