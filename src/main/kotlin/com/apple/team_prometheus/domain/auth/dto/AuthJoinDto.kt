package com.apple.team_prometheus.domain.auth.dto

import jakarta.validation.constraints.NotBlank

class AuthJoinDto {

    data class Response(

        val name: String,

        val status: String
    )


    data class Request (

        @NotBlank(message = "이름은 필수입니다.")
        var name: String,

        @NotBlank(message = "비밀번호는 필수입니다.")
        var password: String,

        @NotBlank(message = "방번호는 필수입니다.")
        var roomNum: Int,

        @NotBlank(message = "생년월일은 필수입니다.")
        val birth: String,

        @NotBlank(message = "입학년도는 필수입니다.")
        var yearOfAdmission: Int
    )
}