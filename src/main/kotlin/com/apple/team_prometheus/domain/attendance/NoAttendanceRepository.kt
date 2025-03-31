package com.apple.team_prometheus.domain.attendance

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface NoAttendanceRepository: JpaRepository<NoAttendance, Long> {

    override fun findAll(): List<NoAttendance>
}