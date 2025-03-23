package com.apple.team_prometheus.global.jwt

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "jwt")
data class JwtProperties(
    var duration: Long,
    var refreshDuration: Long,
    var secretKey: String,
    var issuer: String
)