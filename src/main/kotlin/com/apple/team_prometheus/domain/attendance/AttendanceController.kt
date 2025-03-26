package com.apple.team_prometheus.domain.attendance

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController


@RestController("/api/attendance")
class AttendanceController(
    private val attendanceService: AttendanceService
) {

    @PostMapping("/check/{id}")
    fun checkAttendance(
        @PathVariable("id") userId: Long
    ): ResponseEntity<AttendanceDto.Response> {

        return ResponseEntity.ok(
            attendanceService.checkAttendance(userId)
        )
    }
}