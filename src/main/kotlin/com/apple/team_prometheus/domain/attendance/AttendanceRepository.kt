package com.apple.team_prometheus.domain.attendance

import com.apple.team_prometheus.domain.auth.AuthUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime


@Repository
interface AttendanceRepository: JpaRepository<Attendance, Long> {


    fun findByUserAndAttendanceTimeAndCheckTimeBetween(
        user: AuthUser,
        attendanceTime: AttendanceTime,
        start: LocalDateTime,
        end: LocalDateTime
    ): List<Attendance>
}