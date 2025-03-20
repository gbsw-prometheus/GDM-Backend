package com.apple.team_prometheus.domain.auth

import com.apple.team_prometheus.global.exception.Exceptions
import com.apple.team_prometheus.global.exception.ErrorCode
import com.apple.team_prometheus.global.jwt.*
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration


@Service
class AuthService(
    private val authRepository: AuthRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtProperties: JwtProperties,
    private val jwtProvider: TokenProvider,
    private val jwtRepository: JwtRepository) {


    fun userJoin(joinDto: AuthJoinDto.Request): AuthJoinDto.Response {

        if(authRepository.existsById(joinDto.id)) {
            throw Exceptions(ErrorCode.DUPLICATED_ID)
        }

        val newUser: AuthUser = AuthUser(
            joinDto.id,
            joinDto.password,
            joinDto.name,
            joinDto.roomNum
        )
        newUser.password = passwordEncoder.encode(newUser.password)
        val save: AuthUser = authRepository.save(newUser)

        val response: AuthJoinDto.Response = AuthJoinDto.Response(save.id, save.password)

        return response
    }


    @Transactional
    fun userLogin(loginDto: AuthLoginDto.Request): AuthLoginDto.Response {

        val user: AuthUser? = authRepository.findById(loginDto.id)
            .orElseThrow {
                Exceptions(ErrorCode.USER_NOT_FOUND)
            }

        if (!passwordEncoder.matches(loginDto.password, user!!.password)) {
            throw Exceptions(ErrorCode.INVALID_PASSWORD)
        }



        val token: AccessToken.Response = createAccessToken(user, null)

        val response: AuthLoginDto.Response = AuthLoginDto.Response(user.id, token)
        return response
    }


    private fun createAccessToken(user: AuthUser, refreshToken: String?): AccessToken.Response {
        val tokenDuration: Duration = Duration.ofMinutes(jwtProperties.duration)
        val refreshDuration: Duration = Duration.ofMinutes(jwtProperties.refreshDuration.toLong())

        var savedRefreshToken: RefreshToken? = jwtRepository.findById(user.id)
            .orElse(null)

        if (savedRefreshToken != null && refreshToken != null) {
            if (!savedRefreshToken.getRefreshToken().equals(refreshToken)) return AccessToken.Response(
                "Invalid token.",
                null,
                null
            )
        }

        val accessToken = jwtProvider.generateToken(user, tokenDuration, true)
        val newRefreshToken = jwtProvider.generateToken(user, refreshDuration, false)

        if (savedRefreshToken == null) {
            savedRefreshToken = RefreshToken(user.id, newRefreshToken)
        } else {
            savedRefreshToken.setRefreshToken(newRefreshToken)
        }

        jwtRepository.save(savedRefreshToken)
        return AccessToken.Response("ok", accessToken, newRefreshToken)
    }
}
