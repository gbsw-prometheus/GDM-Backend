package com.apple.team_prometheus.domain.attendance

import com.apple.team_prometheus.domain.auth.AuthUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime


@Repository
interface AttendanceRepository: JpaRepository<Attendance, Long> {

    fun findByUserAndCheckTimeBetween(
        user: AuthUser,
        start: LocalDateTime,
        end: LocalDateTime
    ): List<Attendance>

    fun findByCheckTimeBetween(start: LocalDateTime, end: LocalDateTime): List<Attendance>
}