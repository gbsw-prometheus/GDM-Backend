package com.apple.team_prometheus.domain.attendance

import com.apple.team_prometheus.domain.auth.AuthUser
import jakarta.persistence.*
import java.time.LocalDateTime


@Entity
data class Attendance(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @OneToOne(fetch = FetchType.EAGER)
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