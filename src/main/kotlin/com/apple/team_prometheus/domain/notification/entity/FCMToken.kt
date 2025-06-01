package com.apple.team_prometheus.domain.notification.entity

import com.apple.team_prometheus.domain.auth.entity.AuthUser
import jakarta.persistence.*
import java.time.LocalDateTime


@Entity
class FCMToken(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val tokenId: Long = 0L,

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var user: AuthUser? = null,

    var token: String = "",

    val deviceInfo: String,

    var createDate: LocalDateTime = LocalDateTime.now()
) {
    constructor() : this(
        tokenId = 0L,
        user = AuthUser(),
        deviceInfo = "",
        token = ""
    )
}