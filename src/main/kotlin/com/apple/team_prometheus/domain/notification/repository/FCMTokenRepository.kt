package com.apple.team_prometheus.domain.notification.repository

import com.apple.team_prometheus.domain.auth.entity.AuthUser
import com.apple.team_prometheus.domain.notification.entity.FCMToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FCMTokenRepository: JpaRepository<FCMToken, Long> {

    fun findByUserAndToken(user: AuthUser, token: String): FCMToken?

    fun existsByUserAndToken(user: AuthUser, token: String): Boolean

    fun deleteByToken(token: String) // 추가

    fun findByToken(token: String): FCMToken? // 추가

    fun findAllByUser(user: AuthUser): List<FCMToken>
}