package com.apple.team_prometheus.domain.going.entity

import com.apple.team_prometheus.domain.auth.entity.AuthUser
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.time.LocalDate

@Entity
data class GoingApply(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "users_id", nullable = false)
    @JsonIgnore
    val user: AuthUser,

    @Column(nullable = false)
    var going: Boolean = false,

    @Column(nullable = false)
    val outDateTime: LocalDate,

    @Column(nullable = false)
    val inDateTime: LocalDate,

    @Column(nullable = false)
    val title: String,

    @Column(nullable = false)
    val content: String
) {

    constructor() : this(
        id = 0L,
        user = AuthUser(),
        going = false,
        outDateTime = LocalDate.now(),
        inDateTime = LocalDate.now(),
        title = "",
        content = ""
    )
}