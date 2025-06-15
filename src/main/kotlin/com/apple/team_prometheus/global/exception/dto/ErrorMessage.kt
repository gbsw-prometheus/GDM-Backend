package com.apple.team_prometheus.global.exception.dto

class ErrorMessage {

    data class ErrorResponse(
        val status: Int,
        val message: String
    )
}