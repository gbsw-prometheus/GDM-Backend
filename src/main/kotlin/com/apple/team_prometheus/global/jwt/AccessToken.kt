package com.apple.team_prometheus.global.jwt

class AccessToken {

    data class Response (
        val result: String?,
        val accessToken: String?,
        val refreshToken: String?
    )


    data class Request (
        var id: Long? = null,
        var password: String? = null
    )
}