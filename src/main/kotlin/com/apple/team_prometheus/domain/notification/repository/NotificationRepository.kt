package com.apple.team_prometheus.domain.notification.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import com.apple.team_prometheus.domain.notification.entity.Notification

@Repository
interface NotificationRepository: JpaRepository<Notification, Long> {

    // fun findByAuthor(author: String): List<Notification>
    // fun findByDueDate(dueDate: LocalDate): List<Notification>
}