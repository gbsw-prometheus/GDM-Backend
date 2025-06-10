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

        println(request.requestURI)

        if (isPermitAllPath(request.requestURI)) {
            println("TokenAuthenticationFilter: Permit all path - ${request.requestURI}")
            filterChain.doFilter(request, response)
            return
        }

        val COOKIE_NAME = "accessToken"

        val cookies = request.cookies

        val token = cookies?.firstOrNull {
            it.name == COOKIE_NAME
        }
            ?.value

        println("TokenAuthenticationFilter: Processing ${request.requestURI}")
        println("NEW")
        println("Extracted Token from Cookie: $token")
        println("TokenAuthenticationFilter: Processing ${request.requestURI}")
        println("Extracted Token: $token")

        try {
            if (token != null) {
                val authentication: Authentication = tokenProvider.getAuthentication(token)
                println("Authorities before: ${authentication.authorities}")
                SecurityContextHolder.getContext().authentication = authentication
                println("Authorities after: ${SecurityContextHolder.getContext().authentication?.authorities}")
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

    private fun isPermitAllPath(path: String): Boolean {
        val permitAllPaths = listOf(
            "/wlstmd",
            "/auth/login",
            "/auth/join",
            "/auth/login/token",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/health"
        )
        return permitAllPaths.any { AntPathMatcher().match(it, path) }
    }
}
