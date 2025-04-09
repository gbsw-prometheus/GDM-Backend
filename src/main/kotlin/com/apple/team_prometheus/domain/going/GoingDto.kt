package com.apple.team_prometheus.domain.going

import com.apple.team_prometheus.domain.auth.AuthUser
import java.time.LocalDate



class GoingDto {

    data class GoingRequest(
        val userId: Long,
        val outDateTime: LocalDate,
        val inDateTime: LocalDate,
        val title: String,
        val content: String
    )

    data class GoingResponse(
        val id: Long,
        val userId: AuthUser,
        val going: Boolean
    )

    data class GoingListResponse(
        val id: Long,
        val user: AuthUser,
        val going: Boolean
    )
}