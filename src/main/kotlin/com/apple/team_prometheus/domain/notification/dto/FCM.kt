package com.apple.team_prometheus.domain.notification.dto

class FCM {

    data class Request(
        val token: String,
        val deviceInfo: String?,
    )

    data class Response(
        val result: String
    )
}