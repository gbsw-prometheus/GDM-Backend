package com.apple.team_prometheus.domain.auth.entity

enum class Role {
    STUDENT,
    TEACHER;


    open fun getAuthority(): String {
        return "$this"
    }
}
