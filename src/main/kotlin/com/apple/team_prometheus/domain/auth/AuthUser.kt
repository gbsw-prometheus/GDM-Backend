package com.apple.team_prometheus.domain.auth

import jakarta.persistence.*

@Entity
data class AuthUser(
    @Id
    @Column(nullable = false)
    val id: Long,

    @Column(nullable = false)
    var password: String,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var roomNum: Int

)