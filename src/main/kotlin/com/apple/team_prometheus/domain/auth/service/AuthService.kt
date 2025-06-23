package com.apple.team_prometheus.domain.auth.service

import com.apple.team_prometheus.domain.attendance.dto.AttendanceDto
import com.apple.team_prometheus.domain.attendance.entity.Attendance
import com.apple.team_prometheus.domain.attendance.entity.Status
import com.apple.team_prometheus.domain.auth.entity.Role
import com.apple.team_prometheus.domain.auth.dto.AuthJoinDto
import com.apple.team_prometheus.domain.auth.dto.AuthListDto
import com.apple.team_prometheus.domain.auth.dto.AuthLoginDto
import com.apple.team_prometheus.domain.auth.entity.AuthUser
import com.apple.team_prometheus.domain.auth.repository.AuthRepository
import com.apple.team_prometheus.domain.going.dto.GoingDto
import com.apple.team_prometheus.domain.notification.dto.FCM
import com.apple.team_prometheus.domain.notification.service.FCMTokenService
import com.apple.team_prometheus.global.exception.ErrorCode
import com.apple.team_prometheus.global.exception.Exceptions
import com.apple.team_prometheus.global.jwt.*
import com.apple.team_prometheus.global.jwt.dto.AccessToken
import com.apple.team_prometheus.global.jwt.entity.RefreshToken
import com.apple.team_prometheus.global.jwt.repository.JwtRepository
import io.jsonwebtoken.ExpiredJwtException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.LocalDate
import java.time.Year
import java.time.format.DateTimeFormatter


@Service
class AuthService(
    private val authRepository: AuthRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtProperties: JwtProperties,
    private val jwtProvider: TokenProvider,
    private val jwtRepository: JwtRepository,
    private val fcmTokenService: FCMTokenService
) {

    fun getProfile(
        birth: String,
        name: String
    ): AuthListDto.Response {
        val user = authRepository.findByBirthYearAndName(
            birth = LocalDate.parse(
                birth,
                DateTimeFormatter.ofPattern("yyyy-MM-dd")
            ),
            name = name
        ).orElseThrow {
            Exceptions(errorCode = ErrorCode.USER_NOT_FOUND)
        }

        return AuthListDto.Response(
            id = user.id,
            name = user.name,
            roomNum = user.roomNum,
            role = user.role,
            attendance = user.attendance?.let {
                it.checkTime?.let { it1 ->
                    AttendanceDto.AttendanceListResponse(
                        checkTime = it1,
                    )
                }
            },
            noAttendance = user.noAttendance.map {
                AttendanceDto.NoAttendanceListResponse(
                    attendanceTime = it.attendanceTime
                )
            },
            goingApply = user.goingApply.map {
                GoingDto.ProfileResponse(
                    id = it.id,
                    going = it.going,
                    outDateTime = it.outDateTime,
                    inDateTime = it.inDateTime
                )
            },
            birth = user.birth,
            yearOfAdmission = user.yearOfAdmission,
            isGraduate = user.isGraduate
        )
    }

    fun findAllUsers(): List<AuthListDto.Response> {
        return authRepository.findAllExcludingGoingUsers().map { user ->
            AuthListDto.Response(
                id = user?.id ?: 0L,
                name = user?.name ?: "",
                roomNum = user?.roomNum,
                role = user?.role ?: Role.STUDENT,
                attendance = user?.attendance?.let {
                    it.checkTime?.let { checkTime ->
                        AttendanceDto.AttendanceListResponse(
                            checkTime = checkTime
                        )
                    }
                },
                noAttendance = user?.noAttendance?.map {
                    AttendanceDto.NoAttendanceListResponse(
                        attendanceTime = it.attendanceTime
                    )
                } ?: emptyList(),
                goingApply = user?.goingApply?.map {
                    GoingDto.ProfileResponse(
                        id = it.id,
                        going = it.going,
                        outDateTime = it.outDateTime,
                        inDateTime = it.inDateTime
                    )
                } ?: emptyList(),
                birth = user?.birth ?: LocalDate.now(),
                yearOfAdmission = user?.yearOfAdmission ?: Year.now(),
                isGraduate = user?.isGraduate ?: false
            )
        }
    }

    fun userJoin(
        joinDto: AuthJoinDto.Request
    ): AuthJoinDto.Response {

        // 중복된 이름과 생년월일로 사용자 조회
        val existingUser = authRepository.findByBirthYearAndName(
            birth = LocalDate.parse(
                joinDto.birth,
                DateTimeFormatter.ofPattern("yyyy/MM/dd")
            ),
            name = joinDto.name
        )

        if (existingUser.isPresent) {
            throw Exceptions(
                errorCode = ErrorCode.DUPLICATED,
            )
        }


        val newUser = AuthUser(
           password = passwordEncoder.encode(joinDto.password),
            name = joinDto.name,
            roomNum = joinDto.roomNum,
            role = Role.STUDENT,
            attendance = null,
            noAttendance = emptyList(),
            birth = LocalDate.parse(joinDto.birth, DateTimeFormatter.ofPattern("yyyy/MM/dd")),
            yearOfAdmission = Year.of(joinDto.yearOfAdmission),
            isGraduate = false
        )

        val attendance: Attendance = Attendance(
            user = newUser,
            checkTime = null,
            status = Status.NOT_ATTENDING
        )

        newUser.attendance = attendance

        val savedUser = authRepository.save(newUser)

        val response: AuthJoinDto.Response =  AuthJoinDto.Response(
            name = savedUser.name,
            status = "ok"
        )

        return response
    }

    @Transactional
    fun userLogin(
        loginDto: AuthLoginDto.Request
    ): AuthLoginDto.Response {

        val user = authRepository.findByBirthYearAndName(
            birth = LocalDate.parse(
                loginDto.birth,
                DateTimeFormatter.ofPattern("yyyy/MM/dd")
            ),
            name = loginDto.name
        ).orElseThrow {
            Exceptions(errorCode = ErrorCode.USER_NOT_FOUND)
        }

        if (!passwordEncoder.matches(loginDto.password, user!!.password)) {
            throw Exceptions(
                errorCode = ErrorCode.INVALID_PASSWORD
            )
        }

        val token = createAccessToken(user)

        loginDto.fcmToken?.let {
            fcmTokenService.saveOrUpdateToken(
                user = user,
                fcm = FCM.Request(
                    token = it,
                    deviceInfo = "Unknown Device"
                )
            )
        }

        return AuthLoginDto.Response(user.name, token)
    }

    // 로그인 시 새 토큰 생성
    @Transactional
    fun createAccessToken(user: AuthUser): AccessToken.Response {
        val tokenDuration = Duration.ofMinutes(jwtProperties.duration)
        val refreshDuration = Duration.ofMinutes(jwtProperties.refreshDuration)

        val accessToken = jwtProvider.generateToken(user, tokenDuration, true)
        val refreshToken = jwtProvider.generateToken(user, refreshDuration, false)

        // 기존 리프레시 토큰 삭제 후 새로 저장
        jwtRepository.deleteById(user.id)
        jwtRepository.save(
            RefreshToken(
            userId = user.id,
            refreshToken = refreshToken
        )
        )

        return AccessToken.Response(
            result = "ok",
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }

    // 리프레시 토큰으로 액세스 토큰 갱신 (회전 적용)
    @Transactional
    fun refreshAccessToken(request: String): AccessToken.Response {
        try {
            // 1. 리프레시 토큰 검증
            val claims = jwtProvider.getClaims(request)
            if (claims["type"] != "Refresh") {
                throw Exceptions(
                    errorCode = ErrorCode.INVALID_TOKEN
                )
            }

            // 2. 사용자 조회
            val user = authRepository.findByName(claims.subject)
                .orElseThrow {
                    Exceptions(
                        errorCode = ErrorCode.USER_NOT_FOUND
                    )
                }

            // 3. 저장된 리프레시 토큰 확인
            val storedToken = jwtRepository.findById(user!!.id)
                .orElseThrow {
                    Exceptions(
                        errorCode = ErrorCode.INVALID_TOKEN
                    )
                }

            // 저장된 리프레시 토큰과 요청된 리프레시 토큰이 일치하는지 확인
            if (storedToken.refreshToken != request) {
                throw Exceptions(
                    errorCode = ErrorCode.INVALID_TOKEN
                )
            }


            // 4. 새로운 액세스 토큰 및 리프레시 토큰 생성
            val tokenDuration = Duration.ofMinutes(jwtProperties.duration)
            val refreshDuration = Duration.ofMinutes(jwtProperties.refreshDuration)
            val newAccessToken = jwtProvider.generateToken(user, tokenDuration, true)
            val newRefreshToken = jwtProvider.generateToken(user, refreshDuration, false)


            // 5. 기존 리프레시 토큰 무효화 및 새 토큰 저장
            jwtRepository.delete(storedToken)
            jwtRepository.save(
                RefreshToken(
                userId = user.id,
                refreshToken = newRefreshToken
            )
            )


            return AccessToken.Response(
                result = "ok",
                accessToken =  newAccessToken,
                refreshToken = newRefreshToken
            )

        } catch (e: ExpiredJwtException) {
            return AccessToken.Response("Expired refresh token", null, null)
        } catch (e: Exceptions) {
            return AccessToken.Response(e.message ?: "Invalid token", null, null)
        } catch (e: Exception) {
            return AccessToken.Response("An error occurred: ${e.message}", null, null)
        }
    }
}
