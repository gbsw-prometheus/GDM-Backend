package com.apple.team_prometheus.domain.attendance.entity

import com.apple.team_prometheus.global.exception.ErrorCode
import com.apple.team_prometheus.global.exception.Exceptions

enum class Status {
    ATTENDED,
    NOT_ATTENDING;



    fun changeStatus(): Status {
        return when (this) {
            ATTENDED -> throw Exceptions(
                ErrorCode.ALREADY_ATTENDED
            )
            NOT_ATTENDING -> ATTENDED
        }
    }
}

