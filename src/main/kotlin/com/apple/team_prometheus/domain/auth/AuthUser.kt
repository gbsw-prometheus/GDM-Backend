package com.apple.team_prometheus.domain.auth

import jakarta.persistence.*

@Entity(name = "users")
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

) {
    constructor() : this(0L, "", "", 0)

}