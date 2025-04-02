package com.apple.team_prometheus.domain.attendance

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController


@RestController("/api/attendance")
class AttendanceController(
    private val attendanceService: AttendanceService
) {

    @PostMapping("/check")
    fun checkAttendance(
        @RequestBody request: AttendanceDto.Request
    ): ResponseEntity<AttendanceDto.Response> {

        return ResponseEntity.ok(
            attendanceService.checkAttendance(request)
        )
    }

    @GetMapping("/no-attendance")
    fun getNoAttendanceList(): ResponseEntity<List<NoAttendance>> {
        return ResponseEntity.ok(
            attendanceService.getNoAttendance()
        )
    }
}