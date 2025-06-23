package com.apple.team_prometheus.domain.attendance.entity

import com.apple.team_prometheus.domain.auth.entity.AuthUser
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.time.LocalDateTime


@Entity
data class NoAttendance (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "no_attendance_student", nullable = false)
    val student: AuthUser,

    @Column(nullable = false)
    val attendanceTime:LocalDateTime

) {
    constructor() : this(
        id = 0L,
        student = AuthUser(),
        attendanceTime = LocalDateTime.now())
}