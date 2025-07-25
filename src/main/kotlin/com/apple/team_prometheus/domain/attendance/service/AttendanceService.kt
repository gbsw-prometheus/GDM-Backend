package com.apple.team_prometheus.domain.attendance.service

import com.apple.team_prometheus.domain.attendance.entity.NoAttendance
import com.apple.team_prometheus.domain.attendance.repository.NoAttendanceRepository
import com.apple.team_prometheus.domain.attendance.entity.Status
import com.apple.team_prometheus.domain.attendance.dto.AttendanceDto
import com.apple.team_prometheus.domain.attendance.entity.Attendance
import com.apple.team_prometheus.domain.attendance.repository.AttendanceRepository
import com.apple.team_prometheus.domain.auth.repository.AuthRepository
import com.apple.team_prometheus.domain.auth.entity.AuthUser
import com.apple.team_prometheus.domain.auth.entity.Role
import com.apple.team_prometheus.global.exception.ErrorCode
import com.apple.team_prometheus.global.exception.Exceptions
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime


@Service
class AttendanceService(
    private val attendanceRepository: AttendanceRepository,
    private val authRepository: AuthRepository,
    private val noAttendanceRepository: NoAttendanceRepository
) {

    fun getNoAttendance(): List<NoAttendance> {
        return noAttendanceRepository.findAll()
    }

    fun checkAttendance(request: AttendanceDto.Request): AttendanceDto.Response {
        val user = authRepository.findByBirthYearAndName(
            birth = LocalDate.parse(request.birth),
            name = request.name
        ).orElseThrow {
            Exceptions(
                ErrorCode.USER_NOT_FOUND
            )
        } ?: throw Exceptions(
            ErrorCode.USER_NOT_FOUND
        )

        val now = LocalDateTime.now()
        if (now.hour !in listOf(8, 12, 13, 18, 19) && now.dayOfWeek !in listOf(
                DayOfWeek.SATURDAY,
                DayOfWeek.SUNDAY
            )) {
            throw Exceptions(
                ErrorCode.NOT_ATTENDANCE_TIME

            )
        }

        val attendance = Attendance(
            id = 0L,
            user = user,
            checkTime = now,
            status = user.attendance!!.status.changeStatus()
        )

        val savedAttendance = attendanceRepository.save(attendance)
        val response = AttendanceDto.Response(
            user = savedAttendance.user!!,
            checkTime = savedAttendance.checkTime!!,
            status = savedAttendance.status
        )

        return response
    }

    @Scheduled(cron = "\${schedule.cron.morning}")
    @Transactional
    fun resetMorningAttendance() {
        closeAttendance()
    }

    @Scheduled(cron = "\${schedule.cron.lunch}")
    @Transactional
    fun resetLunchAttendance() {
        closeAttendance()
    }

    @Scheduled(cron = "\${schedule.cron.evening}")
    @Transactional
    fun resetEveningAttendance() {
        closeAttendance()
    }

    fun testAPI(): String {
        closeAttendance()

        return "Test API executed successfully"
    }

    private fun closeAttendance() {
        val students: List<AuthUser?> = authRepository.findAll().filter {
            it?.role == Role.STUDENT &&
            authRepository.findAllExcludingGoingUsers().contains(it)
        }

        students.forEach { student ->
            val attendance = student?.attendance
            if (attendance?.status == Status.NOT_ATTENDING) {
                val noAttendance: NoAttendance = NoAttendance(
                    student = student,
                    attendanceTime = LocalDateTime.now()
                )
                noAttendanceRepository.save(noAttendance)
            }
        }
    }
}