package com.apple.team_prometheus.domain.auth

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*


@Repository
interface AuthRepository : JpaRepository<AuthUser?, Long?> {
    override fun findById(id: Long): Optional<AuthUser?>

    fun findByName(name: String): Optional<AuthUser?>

    override fun existsById(id: Long): Boolean

}