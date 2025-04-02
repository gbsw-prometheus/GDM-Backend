package com.apple.team_prometheus.domain.auth

import com.apple.team_prometheus.global.exception.ErrorCode
import com.apple.team_prometheus.global.exception.Exceptions
import com.apple.team_prometheus.global.jwt.*
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
    private val jwtRepository: JwtRepository
) {

    fun findAllUsers(): MutableList<AuthUser?> {
        return authRepository.findAll()
    }

    fun userJoin(
        joinDto: AuthJoinDto.Request
    ): AuthJoinDto.Response {
        val newUser = AuthUser(
           password = passwordEncoder.encode(joinDto.password),
            name = joinDto.name,
            roomNum = joinDto.roomNum,
            role = Role.STUDENT,
            attendance = emptyList(),
            noAttendance = emptyList(),
            birth = LocalDate.parse(joinDto.birth, DateTimeFormatter.ofPattern("yyyy/MM/dd")),
            yearOfAdmission = Year.of(joinDto.yearOfAdmission),
            isGraduate = false
        )

        val savedUser = authRepository.save(newUser)
        return AuthJoinDto.Response(
            name = savedUser.name,
            status = "ok"
        )
    }

    @Transactional
    fun userLogin(
        loginDto: AuthLoginDto.Request
    ): AuthLoginDto.Response {

        val user = authRepository.findByBirthYearAndName(
            birth = LocalDate.parse(loginDto.birth, DateTimeFormatter.ofPattern("yyyy/MM/dd")),
            name = loginDto.name
        ).orElseThrow { Exceptions(errorCode = ErrorCode.USER_NOT_FOUND) }

        if (!passwordEncoder.matches(loginDto.password, user!!.password)) {
            throw Exceptions(errorCode = ErrorCode.INVALID_PASSWORD)
        }

        val token = createAccessToken(user)
        return AuthLoginDto.Response(user.id, token)
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
        jwtRepository.save(RefreshToken(
            userId = user.id,
            refreshToken = refreshToken
        ))

        return AccessToken.Response("ok", accessToken, refreshToken)
    }

    // 리프레시 토큰으로 액세스 토큰 갱신 (회전 적용)
    @Transactional
    fun refreshAccessToken(request: CreateAccessTokenByRefreshToken): AccessToken.Response {
        try {
            // 1. 리프레시 토큰 검증
            val claims = jwtProvider.getClaims(request.refreshToken)
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
            if (storedToken.refreshToken != request.refreshToken) {
                throw Exceptions(
                    errorCode = ErrorCode.INVALID_TOKEN
                )
            }

            // 4. 새 토큰 생성
            val tokenDuration = Duration.ofMinutes(jwtProperties.duration)
            val refreshDuration = Duration.ofMinutes(jwtProperties.refreshDuration)
            val newAccessToken = jwtProvider.generateToken(user, tokenDuration, true)
            val newRefreshToken = jwtProvider.generateToken(user, refreshDuration, false)

            // 5. 기존 리프레시 토큰 무효화 및 새 토큰 저장
            jwtRepository.delete(storedToken)
            jwtRepository.save(RefreshToken(
                userId = user.id,
                refreshToken = newRefreshToken
            ))

            return AccessToken.Response("ok", newAccessToken, newRefreshToken)
        } catch (e: ExpiredJwtException) {
            return AccessToken.Response("Expired refresh token", null, null)
        } catch (e: Exceptions) {
            return AccessToken.Response(e.message ?: "Invalid token", null, null)
        } catch (e: Exception) {
            return AccessToken.Response("An error occurred: ${e.message}", null, null)
        }
    }
}
