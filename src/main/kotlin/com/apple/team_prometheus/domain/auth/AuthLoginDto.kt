package com.apple.team_prometheus.domain.auth

import com.apple.team_prometheus.global.jwt.AccessToken


class AuthLoginDto {

    data class Response(
        var id: Long,
        var token: AccessToken.Response
    )

    class Request {
        val id: Long = 0

        var password: String = ""
    }
}
