package com.apple.team_prometheus.global.jwt

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id


@Entity
data class RefreshToken(
    @Id
    val userId: Long,
    @Column(nullable = false, length = 1000)
    val refreshToken: String
) {
    constructor() : this(0L, "")
}