package com.apple.team_prometheus.global.jwt.repository

import com.apple.team_prometheus.global.jwt.entity.RefreshToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository



@Repository
interface JwtRepository: JpaRepository<RefreshToken, Long> {

    fun deleteByRefreshToken(refreshToken: String)
}