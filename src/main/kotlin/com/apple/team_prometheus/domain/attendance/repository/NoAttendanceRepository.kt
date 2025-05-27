package com.apple.team_prometheus.domain.attendance.repository

import com.apple.team_prometheus.domain.attendance.entity.NoAttendance
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface NoAttendanceRepository: JpaRepository<NoAttendance, Long> {

    override fun findAll(): List<NoAttendance>
}