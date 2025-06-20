package com.apple.team_prometheus.domain.attendance.controller

import com.apple.team_prometheus.domain.attendance.dto.AttendanceDto
import com.apple.team_prometheus.domain.attendance.service.AttendanceService
import com.apple.team_prometheus.domain.attendance.entity.NoAttendance
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping(value = ["/api/attendance"])
@Tag(name = "Attendance", description = "출석 관련 API")
class AttendanceController(
    private val attendanceService: AttendanceService
) {

    @PostMapping(value = ["/check"])
    @Operation(summary = "출석 체크")
    fun checkAttendance(
        authentication: Authentication
    ): ResponseEntity<AttendanceDto.Response> {

        val request = AttendanceDto.Request(
            birth = authentication.name.split(" ")[0],
            name = authentication.name.split(" ")[1]
        )

        return ResponseEntity.ok(
            attendanceService.checkAttendance(request)
        )
    }

    @GetMapping(value = ["/no-attendance"])
    @Operation(summary = "결석자 리스트 조회")
    fun getNoAttendanceList(): ResponseEntity<List<NoAttendance>> {

        return ResponseEntity.ok(
            attendanceService.getNoAttendance()
        )
    }

    @PostMapping(value = ["/test"])
    @Operation(summary = "테스트 API")
    fun testAPI(): ResponseEntity<String> {

        return ResponseEntity.ok(attendanceService.testAPI())
    }
}