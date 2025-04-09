package com.apple.team_prometheus.global.security

import com.apple.team_prometheus.global.jwt.TokenAuthenticationFilter
import com.apple.team_prometheus.global.jwt.TokenExceptionFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.*
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher



@Configuration
class SecurityConfig(
    private val tokenAuthenticationFilter: TokenAuthenticationFilter,
    private val tokenExceptionFilter: TokenExceptionFilter) {

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
                    ).hasRole("TEACHER")
                    .requestMatchers(
                        AntPathRequestMatcher("/auth/join"),
                        AntPathRequestMatcher("/auth/login"),
                        AntPathRequestMatcher("/auth/login/token"),
                        AntPathRequestMatcher("/swagger-ui.html"),
                        AntPathRequestMatcher("/swagger-ui/**"),
                        AntPathRequestMatcher("/v3/api-docs/**")
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
}