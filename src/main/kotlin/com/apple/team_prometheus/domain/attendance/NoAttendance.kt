package com.apple.team_prometheus.domain.attendance

import com.apple.team_prometheus.domain.auth.AuthUser
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.time.LocalDateTime


@Entity
data class NoAttendance (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @ManyToOne
    @JoinColumn(name = "no_attendance_student", nullable = false)
    val student: AuthUser,

    @Column(nullable = false)
    val attendanceTime:LocalDateTime

) {
    constructor() : this(0L, AuthUser(), LocalDateTime.now())
}