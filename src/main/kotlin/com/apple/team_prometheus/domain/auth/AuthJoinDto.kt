package com.apple.team_prometheus.domain.auth

class AuthJoinDto {

    class Response(val id:Long, var password: String)


    class Request {
        val id: Long = 0

        var password: String = ""

        var name: String = ""

        var roomNum: Int = 0
    }
}