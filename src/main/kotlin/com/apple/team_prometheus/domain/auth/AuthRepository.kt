package com.apple.team_prometheus.domain.auth

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.*


@Repository
interface AuthRepository : JpaRepository<AuthUser?, Long?> {

    @Query("SELECT u FROM users u WHERE u.id NOT IN (SELECT g.user.id FROM GoingApply g WHERE g.going = true)")
    fun findAllExcludingGoingUsers(): List<AuthUser?>

    override fun findById(id: Long): Optional<AuthUser?>

    fun findByName(name: String): Optional<AuthUser?>

    override fun existsById(id: Long): Boolean

    @Query("SELECT u FROM users u " +
            "WHERE u.birth = :birth AND u.name = :name")
    fun findByBirthYearAndName(
        @Param("birth") birth: LocalDate,
        @Param("name") name: String
    ): Optional<AuthUser>
}