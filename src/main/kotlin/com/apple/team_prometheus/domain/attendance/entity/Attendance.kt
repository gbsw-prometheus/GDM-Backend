package com.apple.team_prometheus.domain.attendance.entity

import com.apple.team_prometheus.domain.auth.entity.AuthUser
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.time.LocalDateTime


@Entity
data class Attendance(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id", nullable = false)
    val user: AuthUser?,

    var checkTime: LocalDateTime? = null,

    @Column(nullable = false)
    val status: Status
) {
    constructor(): this(
        id = 0L,
        user = AuthUser(),
        checkTime = null,
        status = Status.NOT_ATTENDING
    )
}