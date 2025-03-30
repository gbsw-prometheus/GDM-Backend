package com.apple.team_prometheus.domain.auth

import com.apple.team_prometheus.global.exception.ErrorCode
import com.apple.team_prometheus.global.exception.Exceptions
import com.apple.team_prometheus.global.jwt.*
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.Year


@Service
class AuthService(
    private val authRepository: AuthRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtProperties: JwtProperties,
    private val jwtProvider: TokenProvider,
    private val jwtRepository: JwtRepository) {


    fun userJoin(
        joinDto: AuthJoinDto.Request
    ): AuthJoinDto.Response {

        val newUser: AuthUser = AuthUser(
            LoginId = joinDto.LoginId,
            password = passwordEncoder.encode(joinDto.password),
            name = joinDto.name,
            roomNum = joinDto.roomNum,
            birthYear = Year.of(joinDto.birthYear),
            yearOfAdmission = Year.of(joinDto.yearOfAdmission),
            isGraduate = false
        )

        val save: AuthUser = authRepository.save(newUser)

        val response: AuthJoinDto.Response = AuthJoinDto.Response(save.id, save.password)

        return response
    }


    @Transactional
    fun userLogin(loginDto: AuthLoginDto.Request): AuthLoginDto.Response {

        val user: AuthUser? = authRepository.findById(loginDto.id)
            .orElseThrow {
                Exceptions(errorCode = ErrorCode.USER_NOT_FOUND)
            }

        if (!passwordEncoder.matches(loginDto.password, user!!.password)) {
            throw Exceptions(errorCode = ErrorCode.INVALID_PASSWORD)
        }


        val token: AccessToken.Response = createAccessToken(user, null)

        val response: AuthLoginDto.Response = AuthLoginDto.Response(user.id, token)
        return response
    }


    private fun createAccessToken(
        user: AuthUser,
        refreshToken: String?
    ): AccessToken.Response {

        var savedRefreshToken: RefreshToken? = jwtRepository.findById(user.id)
            .orElse(null)

        if (savedRefreshToken != null && refreshToken != null) {
            if (!savedRefreshToken.getRefreshToken().equals(refreshToken)) return AccessToken.Response(
                "Invalid token.",
                null,
                null
            )
        }

        val tokenDuration: Duration = Duration.ofMinutes(jwtProperties.duration)
        val refreshDuration: Duration = Duration.ofMinutes(jwtProperties.refreshDuration)
        val accessToken = jwtProvider.generateToken(user, tokenDuration, true)


        val finalRefreshToken = if (savedRefreshToken == null) {
            val newRefreshToken = jwtProvider.generateToken(user, Duration.ofMinutes(jwtProperties.refreshDuration), false)
            savedRefreshToken = RefreshToken(user.id, newRefreshToken)
            jwtRepository.save(savedRefreshToken)
            newRefreshToken
        } else {
            savedRefreshToken.getRefreshToken()
        }

        return AccessToken.Response("ok", accessToken, finalRefreshToken)
    }

    fun refreshAccessToken(
        request: CreateAccessTokenByRefreshToken
    ): AccessToken.Response {
        try {

            val claims: Claims = jwtProvider.getClaims(request.refreshToken)
            val type: String = claims.get("type").toString()
            if (!type.equals("Refresh")) {
                throw Exceptions(errorCode = ErrorCode.INVALID_TOKEN)
            }

            val user: AuthUser = authRepository.findByName(claims.subject)
                .orElseThrow{
                    Exceptions(errorCode = ErrorCode.USER_NOT_FOUND)
                }!!

            return createAccessToken(user, request.refreshToken)
        } catch (e: ExpiredJwtException) {

            return AccessToken.Response(e.message, null, null)
        } catch (e: Exception) {

            return AccessToken.Response(e.message, null, null)
        }
    }


}
