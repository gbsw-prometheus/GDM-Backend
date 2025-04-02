package com.apple.team_prometheus.domain.attendance

import com.apple.team_prometheus.domain.auth.AuthRepository
import com.apple.team_prometheus.domain.auth.Role
import com.apple.team_prometheus.global.exception.ErrorCode
import com.apple.team_prometheus.global.exception.Exceptions
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
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
        } ?: throw IllegalStateException("유저가 null입니다.")

        val now = LocalDateTime.now()
        if (now.hour !in listOf(8, 12, 18)) {
            throw IllegalStateException("지금은 출석 체크 시간이 아닙니다.")
        }

        val attendance = Attendance(
            id = 0L,
            user = user,
            checkTime = now,
            status = Status.ATTENDED
        )

        val savedAttendance = attendanceRepository.save(attendance)
        val response = AttendanceDto.Response(
            user = savedAttendance.user,
            checkTime = savedAttendance.checkTime,
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


    private fun closeAttendance() {
        val students = authRepository.findAll().filter {
            it?.role == Role.STUDENT
        }

        students.forEach { student ->
            student?.attendance?.forEach { attendance ->
                if (attendance.status == Status.NOT_ATTENDING) {
                    val noAttendance: NoAttendance = NoAttendance(
                        student = student,
                        attendanceTime = LocalDateTime.now()
                    )
                    noAttendanceRepository.save(noAttendance)
                }
            }
        }
    }
}