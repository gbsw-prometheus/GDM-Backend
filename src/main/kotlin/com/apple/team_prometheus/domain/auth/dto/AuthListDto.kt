package com.apple.team_prometheus.domain.auth.dto

import com.apple.team_prometheus.domain.attendance.dto.AttendanceDto
import com.apple.team_prometheus.domain.attendance.entity.Attendance
import com.apple.team_prometheus.domain.attendance.entity.NoAttendance
import com.apple.team_prometheus.domain.auth.entity.Role
import com.apple.team_prometheus.domain.going.dto.GoingDto
import com.apple.team_prometheus.domain.going.entity.GoingApply
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.time.LocalDate
import java.time.Year

class AuthListDto {

    data class Response(
        val id: Long = 0L,
        var name: String,
        var roomNum: Int? = null,
        val role: Role,
        var attendance: AttendanceDto.AttendanceListResponse? = null,
        var noAttendance: List<AttendanceDto.NoAttendanceListResponse> = emptyList(),
        var goingApply: List<GoingDto.ProfileResponse> = emptyList(),
        var birth: LocalDate,
        val yearOfAdmission: Year,
        var isGraduate: Boolean,
    )
}