package com.apple.team_prometheus.domain.notification.entity

import com.apple.team_prometheus.domain.auth.entity.AuthUser
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import java.time.LocalDateTime


@Entity
class FCMToken(

    @Id
    val tokenId: Long = 0L,

    @ManyToOne
    var user: AuthUser? = null,

    var token: String = "",

    var createDate: LocalDateTime = LocalDateTime.now()
) {
    constructor() : this(
        tokenId = 0L,
        user = AuthUser(),
        token = ""
    )
}