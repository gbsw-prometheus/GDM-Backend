package com.apple.team_prometheus.global.jwt

import com.fasterxml.jackson.annotation.JsonIgnore

class AccessToken {

    data class Response (
        val result: String?,

        @JsonIgnore
        val accessToken: String?,

        @JsonIgnore
        val refreshToken: String?
    )


    data class Request (
        var id: Long? = null,
        var password: String? = null
    )
}