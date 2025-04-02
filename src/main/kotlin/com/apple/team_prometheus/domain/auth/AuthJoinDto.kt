package com.apple.team_prometheus.domain.auth

class AuthJoinDto {

    data class Response(

        val id:Long,

        val password: String
    )


    data class Request (

        var name: String,

        var password: String,

        var roomNum: Int,

        val birth: String,

        var yearOfAdmission: Int
    )
}