package com.apple.team_prometheus.domain.going

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GoingRepository: JpaRepository<GoingApply, Long> {
}