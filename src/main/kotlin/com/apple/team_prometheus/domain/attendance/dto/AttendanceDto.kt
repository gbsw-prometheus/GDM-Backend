package com.apple.team_prometheus.domain.attendance.dto

import com.apple.team_prometheus.domain.attendance.entity.Status
import com.apple.team_prometheus.domain.auth.entity.AuthUser
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

class AttendanceDto {

    @Schema(name = "AttendanceResponse")
    data class Response(
        val user: AuthUser,
        val checkTime: LocalDateTime,
        val status: Status
    )

    @Schema(name = "AttendanceRequest")
    data class Request(
        val birth: String,
        val name: String,
    )
}