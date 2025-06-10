package com.apple.team_prometheus.global.jwt.dto

data class TokenErrorResponse(val message: String?) {
    var errorMessage: String? = null
}