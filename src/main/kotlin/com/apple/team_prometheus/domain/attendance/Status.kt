package com.apple.team_prometheus.domain.attendance

enum class Status {
    ATTENDED,
    NOT_ATTENDING;



    fun changeStatus(): Status {
        return when (this) {
            ATTENDED -> NOT_ATTENDING
            NOT_ATTENDING -> ATTENDED
        }
    }
}

