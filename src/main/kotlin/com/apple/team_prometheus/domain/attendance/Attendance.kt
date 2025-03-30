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
data class Attendance (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @ManyToOne
    @JoinColumn(name = "users_id", nullable = false)
    val user: AuthUser,

    @Column(nullable = false)
    val checkTime: LocalDateTime,

    @Column(nullable = false)
    val status: Status
) {
    constructor(): this(0L, AuthUser(), LocalDateTime.now(),Status.NOT_ATTENDING)
}