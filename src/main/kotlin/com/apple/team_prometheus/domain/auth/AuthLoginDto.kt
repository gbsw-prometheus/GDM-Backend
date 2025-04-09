package com.apple.team_prometheus.domain.auth

import com.apple.team_prometheus.global.jwt.AccessToken


class AuthLoginDto {

    data class Response(
        var name: String,
        var accessToken: AccessToken.Response
    )

    data class Request (
        var name: String,
        var birth: String,
        var password: String,
    )
}
