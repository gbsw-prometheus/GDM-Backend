package com.apple.team_prometheus

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class TeamPrometheusApplication

fun main(args: Array<String>) {
	runApplication<TeamPrometheusApplication>(*args)
}
