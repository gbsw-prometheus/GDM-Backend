package com.apple.team_prometheus.domain.notification.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import java.time.LocalDate

class NotificationDto {

    @Schema(name = "NotificationResponse")
    data class Response(
        val id: Long,
        val title: String,
        val detail: String,
        val dueDate: LocalDate,
        val author: String,
    )

    @Schema(name = "NotificationResponseWithPage")
    data class ResponseWithPage(
        val content: List<Response>,
        val totalPages: Int,
        val size: Int,
    )

    @Schema(name = "NotificationRequest")
    data class Request(

        @NotBlank
        val title: String,

        @NotBlank
        val detail: String
    )
}