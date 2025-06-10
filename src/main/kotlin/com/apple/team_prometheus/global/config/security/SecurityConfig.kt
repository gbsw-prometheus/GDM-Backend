package com.apple.team_prometheus.global.config.security

import com.apple.team_prometheus.global.jwt.filter.TokenAuthenticationFilter
import com.apple.team_prometheus.global.jwt.filter.TokenExceptionFilter
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
    private val tokenExceptionFilter: TokenExceptionFilter
) {

    @Bean
    @Throws(Exception::class)
    fun filterChain(httpSecurity: HttpSecurity): SecurityFilterChain {
        httpSecurity
            .httpBasic{ it.disable() }
            .csrf{ it.disable() }
            .authorizeHttpRequests { auth ->

                auth
                    //.requestMatchers("/auth/join").hasRole("TEACHER")
                    .requestMatchers(
                        AntPathRequestMatcher("/api/going/**"),
                        AntPathRequestMatcher("/api/attendance/no-attendance"),
                        AntPathRequestMatcher("/api/notifications/**"),
                    ).hasRole("TEACHER")
                    .requestMatchers(
                        AntPathRequestMatcher("/auth/join"),
                        AntPathRequestMatcher("/auth/login"),
                        AntPathRequestMatcher("/auth/login/token"),
                        AntPathRequestMatcher("/wlstmd"),
                        AntPathRequestMatcher("/api/docs"),
                        AntPathRequestMatcher("/api/docs/**"),
                        AntPathRequestMatcher("/swagger-ui/**"),
                        AntPathRequestMatcher("/v3/api-docs/**"),
                        AntPathRequestMatcher("/health"),
                        AntPathRequestMatcher("/error"),
                        AntPathRequestMatcher("/api/meals/**"),
                    ).permitAll()
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