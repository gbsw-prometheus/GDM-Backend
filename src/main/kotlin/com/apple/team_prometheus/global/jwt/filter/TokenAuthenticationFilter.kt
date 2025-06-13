package com.apple.team_prometheus.global.jwt.filter

import com.apple.team_prometheus.global.jwt.repository.JwtRepository
import com.apple.team_prometheus.global.jwt.TokenProvider
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.security.SignatureException
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.util.AntPathMatcher
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

@Component
class TokenAuthenticationFilter(
    private val tokenProvider: TokenProvider,
    private val jwtRepository: JwtRepository
) : OncePerRequestFilter() {

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {

        val COOKIE_NAME = "accessToken"

        val cookies = request.cookies


        val token = cookies?.firstOrNull {
            it.name == COOKIE_NAME
        }
            ?.value

        println(token)

        try {
            if (token != null) {
                val authentication: Authentication = tokenProvider.getAuthentication(token)
                SecurityContextHolder.getContext().authentication = authentication
            } else {
                println("TokenAuthenticationFilter: No token provided")
            }
            filterChain.doFilter(request, response)
        } catch (e: IllegalArgumentException) {

            println("TokenAuthenticationFilter: Invalid Token - ${e.message}")
            throw JwtException("Invalid Token. 유효하지 않은 토큰")

        } catch (e: ExpiredJwtException) {

            println("TokenAuthenticationFilter: Expired Token - ${e.message}")

            val claims = e.claims
            val userId = claims["id"] as? Long
            if (userId != null) {
                jwtRepository.deleteById(userId)
            }

            throw JwtException("Expired token, 토큰 기한 만료")

        } catch (e: SignatureException) {

            println("TokenAuthenticationFilter: Signature Failed - ${e.message}")
            throw JwtException("Signature Failed. 인증실패")

        }
    }
}
