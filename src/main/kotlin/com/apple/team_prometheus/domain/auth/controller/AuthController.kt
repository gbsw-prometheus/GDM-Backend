package com.apple.team_prometheus.domain.auth.controller

import com.apple.team_prometheus.domain.auth.dto.AuthJoinDto
import com.apple.team_prometheus.domain.auth.dto.AuthListDto
import com.apple.team_prometheus.domain.auth.dto.AuthLoginDto
import com.apple.team_prometheus.domain.auth.service.AuthService
import com.apple.team_prometheus.domain.auth.entity.AuthUser
import com.apple.team_prometheus.global.jwt.dto.AccessToken
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Duration


@RestController
@RequestMapping(value = ["/api/auth"])
@Tag(name = "Auth", description = "회원가입 관련 API")
class AuthController(val authService: AuthService) {

    @PostMapping(value = ["/login"])
    @Operation(summary = "로그인")
    fun login(
        @Valid @RequestBody loginDto: AuthLoginDto.Request,
        response: HttpServletResponse
    ): ResponseEntity<AuthLoginDto.Response> {

        val userResponse: AuthLoginDto.Response = authService.userLogin(loginDto)

        val cookie = userResponse.token.refreshToken?.let {
            Cookie("refreshToken", it).apply {
                isHttpOnly = true
                secure = true
                path = "/"
                maxAge = Duration.ofDays(7).seconds.toInt()
            }
        }
        cookie?.let { response.addCookie(it) }

        val accessTokenCookie = userResponse.token.accessToken?.let {
            Cookie("accessToken", it).apply {
                isHttpOnly = true
                secure = true
                path = "/"
                maxAge = Duration.ofMinutes(15).seconds.toInt() // Access Token의 만료 시간에 맞게 설정
            }
        }
        accessTokenCookie?.let { response.addCookie(it) }


        return ResponseEntity.ok(
            userResponse
        )
    }

    @PostMapping(value = ["/join"])
    @Operation(summary = "회원가입")
    fun join(
        @Valid @RequestBody joinDto: AuthJoinDto.Request
    ): ResponseEntity<AuthJoinDto.Response> {

        return  ResponseEntity.ok(
            authService.userJoin(joinDto)
        )
    }

    @PostMapping(value = ["/login/token"])
    @Operation(summary = "Access Token 재발급")
    fun tokenRefresh(
        @CookieValue(value = "refreshToken", required = false) refreshToken: String,
        response: HttpServletResponse
    ): ResponseEntity<AccessToken.Response> {

        val accessTokenResponse = authService.refreshAccessToken(refreshToken)

        accessTokenResponse.refreshToken?.let {
            val refreshTokenCookie = Cookie("refreshToken", it).apply {
                isHttpOnly = true
                secure = true
                path = "/"
                maxAge = Duration.ofDays(7).seconds.toInt()
            }
            response.addCookie(refreshTokenCookie)
        }

        accessTokenResponse.accessToken?.let {
            val accessTokenCookie = Cookie("accessToken", it).apply {
                isHttpOnly = true
                secure = true
                path = "/"
                maxAge = Duration.ofMinutes(15).seconds.toInt()
            }
            response.addCookie(accessTokenCookie)
        }

        return ResponseEntity.ok(accessTokenResponse)
    }


    @GetMapping(value = ["/users"])
    @Operation(summary = "모든 사용자 조회")
    fun findAllUsers(): ResponseEntity<List<AuthListDto.Response>> {
        return ResponseEntity.ok(
            authService.findAllUsers()
        )
    }

    @GetMapping(value = ["/user"])
    @Operation(summary = "현재 사용자 조회")
    fun getProfile(
        authentication: Authentication
    ): ResponseEntity<AuthListDto.Response> {

        println(authentication)
        return ResponseEntity.ok(
            authService.getProfile(
                authentication.name.split(" ")[0], // birth
                authentication.name.split(" ")[1] // name
            )
        )
    }
}