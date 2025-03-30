package com.apple.team_prometheus.domain.attendance

import com.apple.team_prometheus.domain.auth.AuthRepository
import com.apple.team_prometheus.global.exception.ErrorCode
import com.apple.team_prometheus.global.exception.Exceptions
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Year


@Service
class AttendanceService (
    private val attendanceRepository: AttendanceRepository,
    private val authRepository: AuthRepository
) {


    fun checkAttendance(request: AttendanceDto.Request): AttendanceDto.Response {
        val user = authRepository.findByBirthYearAndNameAndYearOfAdmission(
            birthYear = Year.of(request.birthYear),
            name = request.name,
            yearOfAdmission = Year.of(request.yearOfAdmission)
        )
            .orElseThrow {
                Exceptions(ErrorCode.USER_NOT_FOUND)
            }
            ?: throw IllegalStateException("유저가 null입니다.")


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

        // 저장
        val savedAttendance = attendanceRepository.save(attendance)

        // 응답 반환
        return AttendanceDto.Response(
            user = savedAttendance.user,
            checkTime = savedAttendance.checkTime,
            status = savedAttendance.status
        )

    }
}