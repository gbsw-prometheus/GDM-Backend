package com.apple.team_prometheus.domain.auth.dto

import com.apple.team_prometheus.domain.notification.entity.FCMToken
import com.apple.team_prometheus.global.jwt.AccessToken


class AuthLoginDto {

    data class Response(
        var name: String,
        var token: AccessToken.Response
    )

    data class Request (
        var name: String,
        var birth: String,
        var password: String,
        var fcmToken: String? = null
    )
}
