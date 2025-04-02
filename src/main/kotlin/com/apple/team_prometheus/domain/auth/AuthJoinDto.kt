package com.apple.team_prometheus.domain.auth

class AuthJoinDto {

    data class Response(

        val name: String,

        val status: String
    )


    data class Request (

        var name: String,

        var password: String,

        var roomNum: Int,

        val birth: String,

        var yearOfAdmission: Int
    )
}