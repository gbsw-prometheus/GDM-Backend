package com.apple.team_prometheus.domain.attendance.dto

import com.apple.team_prometheus.domain.attendance.entity.Status
import com.apple.team_prometheus.domain.auth.entity.AuthUser
import java.time.LocalDateTime

class AttendanceDto {

    data class Response(
        val user: AuthUser,
        val checkTime: LocalDateTime,
        val status: Status
    )

    data class Request(
        val birth: String,
        val name: String,
        val yearOfAdmission: Int
    )
}