package com.apple.team_prometheus.domain.going.dto

import com.apple.team_prometheus.domain.auth.entity.AuthUser
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate



class GoingDto {

    @Schema(name = "GoingRequest")
    data class Request(
        var userName: String?,
        var userBirth: String?,
        val outDateTime: String,
        val inDateTime: String,
        val title: String,
        val content: String
    )

    @Schema(name = "GoingResponse")
    data class Response(
        val id: Long,
        val userId: AuthUser,
        val going: Boolean
    )

    @Schema(name = "GoingListResponse")
    data class ListResponse(
        val id: Long,
        val user: AuthUser,
        val going: Boolean
    )

    @Schema(name = "ProfileGoingResponse")
    data class ProfileResponse(
        val id: Long,
        val going: Boolean,
        val outDateTime: LocalDate,
        val inDateTime: LocalDate,
    )


}