package com.apple.team_prometheus

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Profile
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class TeamPrometheusApplicationTests {

	@Test
	@Profile("!test")
	fun contextLoads() {

	}

}
