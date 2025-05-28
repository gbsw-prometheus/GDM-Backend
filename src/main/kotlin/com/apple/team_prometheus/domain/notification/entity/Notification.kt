package com.apple.team_prometheus.domain.notification.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.LocalDate

@Entity
data class Notification (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(nullable = false)
    var title: String,

    @Column(nullable = false)
    var detail: String,

    @Column(nullable = false)
    var author: String,

    @Column(nullable = false)
    var dueDate: LocalDate
) {
    constructor() : this(
        id = 0L,
        title = "",
        detail = "",
        author = "",
        dueDate = LocalDate.now()
    )
}