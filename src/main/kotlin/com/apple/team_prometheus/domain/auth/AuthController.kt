package com.apple.team_prometheus.domain.auth

import com.apple.team_prometheus.global.jwt.AccessToken
import com.apple.team_prometheus.global.jwt.CreateAccessTokenByRefreshToken
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
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
    fun login(
        @RequestBody loginDto: AuthLoginDto.Request
    ): ResponseEntity<AuthLoginDto.Response> {

        return ResponseEntity.ok(
            authService.userLogin(loginDto)
        )
    }

    @PostMapping(value = ["/join"])
    fun join(
        @RequestBody joinDto: AuthJoinDto.Request
    ): ResponseEntity<AuthJoinDto.Response> {

        return  ResponseEntity.ok(
            authService.userJoin(joinDto)
        )
    }

    @PostMapping(value = ["/login/token"])
    fun tokenRefresh(
        @RequestBody refreshToken: CreateAccessTokenByRefreshToken
    ): ResponseEntity<AccessToken.Response> {

        return ResponseEntity.ok(
            authService.refreshAccessToken(refreshToken)
        )
    }


    @GetMapping(value = ["/users"])
    fun findAllUsers(): ResponseEntity<MutableList<AuthUser?>> {
        return ResponseEntity.ok(
            authService.findAllUsers()
        )
    }
}