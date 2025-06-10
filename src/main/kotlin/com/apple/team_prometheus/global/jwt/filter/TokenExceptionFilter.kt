package com.apple.team_prometheus.global.jwt.filter

import com.apple.team_prometheus.global.jwt.dto.TokenErrorResponse
import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.JwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException


@Component
class TokenExceptionFilter : OncePerRequestFilter() {

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        println("TokenExceptionFilter: Processing ${request.requestURI}")
        try {
            filterChain.doFilter(request, response)
        } catch (e: JwtException) {
            println("TokenExceptionFilter: Exception caught - ${e.message}")
            setErrorResponse(response, e)
        }
    }

    @Throws(IOException::class)
    fun setErrorResponse(response: HttpServletResponse, throwable: Throwable) {
        response.status = HttpStatus.UNAUTHORIZED.value()
        response.contentType = "application/json; charset=UTF-8"

        val jwtExceptionResponse = TokenErrorResponse(throwable.message)
        val mapper = ObjectMapper()

        response.writer.write(mapper.writeValueAsString(jwtExceptionResponse))
    }
}