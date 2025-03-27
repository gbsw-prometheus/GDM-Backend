package com.apple.team_prometheus.domain.attendance

import com.apple.team_prometheus.domain.auth.AuthRepository
import com.apple.team_prometheus.global.exception.ErrorCode
import com.apple.team_prometheus.global.exception.Exceptions
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime


@Service
class AttendanceService (
    private val attendanceRepository: AttendanceRepository,
    private val authRepository: AuthRepository
) {


    fun checkAttendance(userId: Long): AttendanceDto.Response {
        val user = authRepository.findById(userId)
            .orElseThrow{
                Exceptions(
                    errorCode = ErrorCode.USER_NOT_FOUND
                )
            }


        val attendanceTime: AttendanceTime = getTime()
            ?: throw IllegalStateException("현재 출석시간이 아님")

        val todayStart: LocalDateTime = LocalDate.now().atStartOfDay()
        val todayEnd: LocalDateTime = LocalDate.now().atTime(LocalTime.MAX)

        val alreadyPresent: List<Attendance> = attendanceRepository.findByUserAndAttendanceTimeAndCheckTimeBetween(
            user = user!!,
            attendanceTime = attendanceTime,
            start = todayStart,
            end = todayEnd
        )

        if (alreadyPresent.isNotEmpty()) {
            throw IllegalStateException(
                "${attendanceTime} 시간대에 이미 출석을 체크했습니다"
            )
        }

        val attendance: Attendance = Attendance(
            id = 0L,
            user = user,
            checkTime = LocalDateTime.now(),
            status = Status.ATTENDED,
            attendanceTime = attendanceTime
        )

        val checked: Attendance = attendanceRepository.save(attendance)

        return AttendanceDto.Response(
            user = checked.user,
            checkTime = checked.checkTime,
            status = checked.status
        )
    }


    fun getTime(): AttendanceTime? {
        val now = LocalDateTime.now()

        return when {
            now.hour in 13..13 -> AttendanceTime.LUNCH
            now.hour in 20..20 -> AttendanceTime.EVENING
            else -> null
        }
    }
}