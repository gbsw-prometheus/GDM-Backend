package com.apple.team_prometheus.global.jwt

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository



@Repository
interface JwtRepository: JpaRepository<RefreshToken, Long> {

}