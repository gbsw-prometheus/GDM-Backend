package com.apple.team_prometheus.domain.notification.dto

import java.time.LocalDate

class NotificationDto {

    data class Response(
        val id: Long,
        val title: String,
        val detail: String,
        val dueDate: LocalDate,
        val author: String
    )

    data class Request(
        val title: String,
        val detail: String
    )
}