package com.apple.team_prometheus.domain.attendance

import com.apple.team_prometheus.domain.auth.AuthRepository
import com.apple.team_prometheus.global.exception.ErrorCode
import com.apple.team_prometheus.global.exception.Exceptions
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime


@Service
class AttendanceService (
    private val attendanceRepository: AttendanceRepository,
    private val authRepository: AuthRepository
) {


    fun checkAttendance(userId: Long): AttendanceDto.Response {
        val user = authRepository.findById(userId)
            .orElseThrow{
                Exceptions(errorCode = ErrorCode.USER_NOT_FOUND)
            }

        val attendance: Attendance = Attendance(
            id = 0L,
            user = user!!,
            checkTime = LocalDateTime.now(),
            status = Status.ATTENDED
        )

        val checked: Attendance = attendanceRepository.save(attendance)

        return AttendanceDto.Response(
            user = checked.user,
            checkTime = checked.checkTime,
            status = checked.status
        )
    }
}