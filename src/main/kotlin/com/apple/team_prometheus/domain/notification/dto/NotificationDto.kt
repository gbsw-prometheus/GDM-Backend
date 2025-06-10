package com.apple.team_prometheus.domain.notification.dto

import jakarta.validation.constraints.NotBlank
import java.time.LocalDate

class NotificationDto {

    data class Response(
        val id: Long,
        val title: String,
        val detail: String,
        val dueDate: LocalDate,
        val author: String,
    )

    data class ResponseWithPage(
        val content: List<Response>,
        val totalPages: Int,
        val size: Int,
    )

    data class Request(

        @NotBlank
        val title: String,

        @NotBlank
        val detail: String
    )
}