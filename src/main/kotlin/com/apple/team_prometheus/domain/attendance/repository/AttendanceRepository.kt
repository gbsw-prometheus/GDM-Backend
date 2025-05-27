package com.apple.team_prometheus.domain.attendance.repository

import com.apple.team_prometheus.domain.attendance.entity.Attendance
import com.apple.team_prometheus.domain.auth.entity.AuthUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface AttendanceRepository: JpaRepository<Attendance, Long> {

    fun findByUser(user: AuthUser): List<Attendance>

}