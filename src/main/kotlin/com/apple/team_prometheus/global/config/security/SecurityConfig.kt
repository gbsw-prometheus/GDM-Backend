package com.apple.team_prometheus.global.config.security

import com.apple.team_prometheus.global.jwt.filter.TokenAuthenticationFilter
import com.apple.team_prometheus.global.jwt.filter.TokenExceptionFilter
import com.apple.team_prometheus.global.jwt.repository.JwtRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.annotation.web.configurers.*
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher


@Configuration
class SecurityConfig(
    private val tokenAuthenticationFilter: TokenAuthenticationFilter,
    private val tokenExceptionFilter: TokenExceptionFilter,
    private val jwtRepository: JwtRepository
) {

    @Bean
    @Throws(Exception::class)
    fun filterChain(httpSecurity: HttpSecurity): SecurityFilterChain {
        httpSecurity
            .httpBasic{ it.disable() }
            .csrf{ it.disable() }
            .logout { logout ->
                logout.logoutUrl("/api/auth/logout")
                    .addLogoutHandler { request, response, _ ->
                        request.cookies?.forEach { cookie ->
                            if (cookie.name == "refreshToken") {
                                cookie.apply {
                                    maxAge = 0
                                    value = ""
                                }
                                response.addCookie(cookie)
                                // DB에서 RefreshToken 삭제
                                val refreshToken = cookie.value
                                if (refreshToken.isNotEmpty()) {
                                    jwtRepository.deleteByRefreshToken(refreshToken)
                                }
                            }
                        }
                    }
            }
            .authorizeHttpRequests { auth ->

                auth
                    .requestMatchers(
                        AntPathRequestMatcher("/api/auth/join"),
                        AntPathRequestMatcher("/api/going/**"),
                        AntPathRequestMatcher("/api/attendance/no-attendance"),
                        AntPathRequestMatcher("/api/notifications/**"),
                        AntPathRequestMatcher("/api/auth/users"),
                    ).hasRole("TEACHER")
                    .requestMatchers(
                        AntPathRequestMatcher("/api/auth/login"),
                        AntPathRequestMatcher("/api/auth/login/token"),
                        AntPathRequestMatcher("/wlstmd"),
                        AntPathRequestMatcher("/api/docs"),
                        AntPathRequestMatcher("/api/docs/**"),
                        AntPathRequestMatcher("/swagger-ui/**"),
                        AntPathRequestMatcher("/v3/api-docs/**"),
                        AntPathRequestMatcher("/health"),
                        AntPathRequestMatcher("/error"),
                    ).permitAll()
                    .anyRequest().authenticated()
            }
            .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .addFilterBefore(tokenExceptionFilter, TokenAuthenticationFilter::class.java)
        return httpSecurity.build();
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder{
        return BCryptPasswordEncoder()
    }

    @Bean
    fun webSecurityCustomizer(): WebSecurityCustomizer {
        return WebSecurityCustomizer { web ->
            web.ignoring().requestMatchers(
                AntPathRequestMatcher("/swagger-ui/**"),
                AntPathRequestMatcher("/v3/api-docs/**"),
                AntPathRequestMatcher("/swagger-resources/**"),
                AntPathRequestMatcher("/error"),
            )
        }
    }
}