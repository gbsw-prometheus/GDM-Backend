package com.apple.team_prometheus.domain.attendance

import com.apple.team_prometheus.domain.auth.AuthUser
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import java.time.LocalDateTime


@Entity
data class Attendance(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @OneToOne
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