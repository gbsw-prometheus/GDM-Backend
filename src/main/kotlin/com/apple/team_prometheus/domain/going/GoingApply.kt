package com.apple.team_prometheus.domain.going

import com.apple.team_prometheus.domain.auth.AuthUser
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
data class GoingApply(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    val user: AuthUser,

    @Column(nullable = false)
    var going: Boolean = false,

    @Column(nullable = false)
    val outDateTime: LocalDateTime,

    @Column(nullable = false)
    val inDateTime: LocalDateTime,

    @Column(nullable = false)
    val title: String,

    @Column(nullable = false)
    val content: String
) {
    constructor() : this(0L, AuthUser(), false, LocalDateTime.now(), LocalDateTime.now(), "", "")
}