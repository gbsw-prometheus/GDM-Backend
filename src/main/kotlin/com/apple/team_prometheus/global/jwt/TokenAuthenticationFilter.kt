package com.apple.team_prometheus.global.jwt

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
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException


@Component
class TokenAuthenticationFilter(
    private val tokenProvider: TokenProvider
) : OncePerRequestFilter() {

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val HEADER = "Authorization"
        val authorizationHeader = request.getHeader(HEADER)
        val token = getAccessToken(authorizationHeader)
        println("TokenAuthenticationFilter: Processing ${request.requestURI}")
        println("Authorization Header: $authorizationHeader")
        println("Extracted Token: $token")

        try {
            if (token != null) {
                println("TokenAuthenticationFilter: Validating token")
                val authentication: Authentication = tokenProvider.getAuthentication(token)
                println("Authentication: $authentication")
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
            throw JwtException("Expired token, 토큰 기한 만료")

        } catch (e: SignatureException) {

            println("TokenAuthenticationFilter: Signature Failed - ${e.message}")
            throw JwtException("Signature Failed. 인증실패")

        }
    }

    private fun getAccessToken(authorizationHeader: String?): String? {
        val PREFIX = "Bearer "
        return if (authorizationHeader != null && authorizationHeader.startsWith(PREFIX)) {
            authorizationHeader.substring(PREFIX.length)
        } else {
            null
        }
    }
}
