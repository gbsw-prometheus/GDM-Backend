package com.apple.team_prometheus.domain.auth.dto

import com.apple.team_prometheus.global.jwt.dto.AccessToken
import io.swagger.v3.oas.annotations.media.Schema


class AuthLoginDto {

    @Schema(name = "AuthLoginResponse")
    data class Response(
        var name: String,
        var token: AccessToken.Response
    )

    @Schema(name = "AuthLoginRequest")
    data class Request (
        var name: String,
        var birth: String,
        var password: String,
        var fcmToken: String? = null
    )
}
