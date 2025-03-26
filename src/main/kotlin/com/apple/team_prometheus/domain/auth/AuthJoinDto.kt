package com.apple.team_prometheus.domain.auth

class AuthJoinDto {

    data class Response(
        val id:Long,
        val password: String
    )


    class Request {
        val id: Long = 0

        var password: String = ""

        var name: String = ""

        var roomNum: Int = 0
    }
}