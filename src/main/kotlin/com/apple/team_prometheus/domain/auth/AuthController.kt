package com.apple.team_prometheus.domain.auth

import com.apple.team_prometheus.global.jwt.AccessToken
import com.apple.team_prometheus.global.jwt.CreateAccessTokenByRefreshToken
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping(value = ["/auth"])
@Tag(name = "Auth")
class AuthController(val authService: AuthService) {

    @PostMapping(value = ["/login"])
    @Operation(summary = "로그인")
    fun login(
        @RequestBody loginDto: AuthLoginDto.Request,
        response: HttpServletResponse
    ): ResponseEntity<AuthLoginDto.Response> {

        val userResponse: AuthLoginDto.Response = authService.userLogin(loginDto)

        val cookie: Cookie = Cookie("refreshToken", userResponse.token.refreshToken)
        cookie.isHttpOnly = true
        cookie.secure = true
        cookie.path = "/"
        cookie.maxAge = 60 * 60 * 24 * 7 // 7 days
        cookie.attributes["SameSite"] = "None"
        response.addCookie(cookie)


        return ResponseEntity.ok(
            userResponse
        )
    }

    @PostMapping(value = ["/join"])
    @Operation(summary = "회원가입")
    fun join(
        @RequestBody joinDto: AuthJoinDto.Request
    ): ResponseEntity<AuthJoinDto.Response> {

        return  ResponseEntity.ok(
            authService.userJoin(joinDto)
        )
    }

    @PostMapping(value = ["/login/token"])
    @Operation(summary = "Access Token 재발급")
    fun tokenRefresh(
        @CookieValue(value = "refreshToken", required = false) refreshToken: String
    ): ResponseEntity<AccessToken.Response> {


        return ResponseEntity.ok(
            authService.refreshAccessToken(refreshToken)
        )
    }


    @GetMapping(value = ["/users"])
    @Operation(summary = "모든 사용자 조회")
    fun findAllUsers(): ResponseEntity<List<AuthUser?>> {
        return ResponseEntity.ok(
            authService.findAllUsers()
        )
    }
}