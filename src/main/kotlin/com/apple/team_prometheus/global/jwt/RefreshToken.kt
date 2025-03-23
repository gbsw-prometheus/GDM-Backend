package com.apple.team_prometheus.global.jwt

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id


@Entity
data class RefreshToken(
    @field:Column(
        updatable = true, nullable = false
    ) private val userId: Long,

    @field:Column(
        nullable = false
    ) private var refreshToken: String

) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private val id: Long? = null

    fun getRefreshToken(): String = refreshToken

    fun setRefreshToken(newRefreshToken: String) {
        this.refreshToken = newRefreshToken
    }

}