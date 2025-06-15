package com.apple.team_prometheus.global.exception

import com.apple.team_prometheus.global.exception.ErrorCode


class Exceptions(errorCode: ErrorCode) : RuntimeException(errorCode.errorMessage) {
    val errorCode: ErrorCode = errorCode
}