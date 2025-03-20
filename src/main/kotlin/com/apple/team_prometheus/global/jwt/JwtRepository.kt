package com.apple.team_prometheus.global.jwt

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*


@Repository
interface JwtRepository: JpaRepository<RefreshToken, Long> {
    override fun findById(id: Long): Optional<RefreshToken>
}