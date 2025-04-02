package com.apple.team_prometheus.domain.auth

class AuthJoinDto {

    data class Response(
        val id:Long,
        val password: String
    )


    data class Request (
        val LoginId: Long = 0,

        var password: String = "",

        var name: String = "",

        var roomNum: Int = 0,

        val birthYear: Int = 0,

        var yearOfAdmission: Int = 0
    )
}