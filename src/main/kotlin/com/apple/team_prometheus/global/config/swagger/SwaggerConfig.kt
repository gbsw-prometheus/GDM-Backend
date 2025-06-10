package com.apple.team_prometheus.global.config.swagger

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {
    //http://localhost:8080/wlstmd
    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .components(Components())
            .info(info())
    }

    private fun info(): Info {
        return Info()
            .title("유유진진승승 API")
            .description("유진승과 성홍제를 위한 API 문서입니다.")
            .version("1.0")
    }
}