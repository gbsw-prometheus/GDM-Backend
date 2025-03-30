package com.apple.team_prometheus.domain.auth

import jakarta.persistence.*
import java.time.LocalDate
import java.time.Year

@Entity(name = "users")
data class AuthUser(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(nullable = false)
    val LoginId: Long,

    @Column(nullable = false)
    var password: String,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var roomNum: Int,

    @Column(nullable = false)
    var birthYear: Year,

    @Column(nullable = false)
    val yearOfAdmission: Year,

    @Column(nullable = false)
    var isGraduate: Boolean

) {
    constructor() : this(0L, 0L, "", "", 0, Year.now(), Year.now(), false)

}