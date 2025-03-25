package com.apple.team_prometheus

import com.apple.team_prometheus.global.jwt.JwtProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties::class)
class TeamPrometheusApplication

fun main(args: Array<String>) {
	runApplication<TeamPrometheusApplication>(*args)
}
