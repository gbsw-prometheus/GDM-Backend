package com.apple.team_prometheus.domain.auth.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

class AuthJoinDto {

    @Schema(name = "AuthJoinResponse")
    data class Response(

        val name: String,

        val status: String
    )

    @Schema(name = "AuthJoinRequest")
    data class Request (

        @NotBlank(message = "이름은 필수입니다.")
        var name: String,

        @NotBlank(message = "비밀번호는 필수입니다.")
        var password: String,

        @NotNull(message = "방번호는 필수입니다.")
        var roomNum: Int,

        @NotBlank(message = "생년월일은 필수입니다.")
        val birth: String,

        @NotNull(message = "입학년도는 필수입니다.")
        var yearOfAdmission: Int
    )
}