package com.apple.team_prometheus.domain.notification.controller

import com.apple.team_prometheus.domain.auth.entity.AuthUser
import com.apple.team_prometheus.domain.notification.dto.FCM
import com.apple.team_prometheus.domain.notification.service.NotificationService
import com.apple.team_prometheus.domain.notification.dto.NotificationDto
import com.apple.team_prometheus.domain.notification.service.FCMTokenService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping(value = ["/api/notifications"])
@Tag(name = "Notification", description = "알림 관련 API")
class NotificationController(
    private val notificationService: NotificationService,
    private val fcmTokenService: FCMTokenService
) {

    @PostMapping(value = ["/fcm-register"])
    @Operation(summary = "FCM토큰 발급")
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
    @Operation(summary = "알림 생성 및 푸시 전송")
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

    @GetMapping(value = ["/get-all"])
    @Operation(summary = "모든 알림 조회")
    fun getAllNotifications(
        @PageableDefault(size = 10, sort = ["id"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<NotificationDto.ResponseWithPage> {
        val response = notificationService.getAllNotifications(pageable)

        if (response.content.isEmpty()) {
            return ResponseEntity.noContent().build()
        }

        return ResponseEntity.ok(response)
    }


    @PutMapping(value = ["/update/{id}"])
    @Operation(summary = "알림 수정")
    fun updateNotification(
        @PathVariable("id") id: Long,
        @Valid @RequestBody request: NotificationDto.Request,
    ): ResponseEntity<NotificationDto.Response> {

        val updatedNotification = notificationService.updateNotification(id, request)

        return ResponseEntity.ok(updatedNotification)
    }

    @DeleteMapping(value = ["/delete/{id}"])
    @Operation(summary = "알림 삭제")
    fun deleteNotification(
        @PathVariable("id") id: Long,
    ): ResponseEntity<String> {

        notificationService.deleteNotification(id)

        return ResponseEntity.ok("알림이 성공적으로 삭제되었습니다.")
    }
}
