package com.apple.team_prometheus.global.jwt

data class TokenErrorResponse(val message: String?) {
    var errorMessage: String? = null
}