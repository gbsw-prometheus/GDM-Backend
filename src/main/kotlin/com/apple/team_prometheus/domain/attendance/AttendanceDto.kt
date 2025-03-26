package com.apple.team_prometheus.domain.attendance

import com.apple.team_prometheus.domain.auth.AuthUser
import java.time.LocalDateTime

class AttendanceDto {

    data class Response(
        val user: AuthUser,
        val checkTime: LocalDateTime,
        val status: Status
    )
}