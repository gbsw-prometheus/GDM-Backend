package com.apple.team_prometheus

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TeamPrometheusApplication

fun main(args: Array<String>) {
	runApplication<TeamPrometheusApplication>(*args)
}
