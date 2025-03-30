package com.apple.team_prometheus.domain.attendance

import com.apple.team_prometheus.domain.auth.AuthUser
import java.time.LocalDateTime
import java.time.Year

class AttendanceDto {

    data class Response(
        val user: AuthUser,
        val checkTime: LocalDateTime,
        val status: Status
    )

    data class Request(
        val birthYear: Int,
        val name: String,
        val yearOfAdmission: Int
    )
}