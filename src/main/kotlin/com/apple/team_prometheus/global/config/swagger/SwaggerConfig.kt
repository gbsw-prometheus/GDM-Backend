package com.apple.team_prometheus.global.config.swagger

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {
    //http://localhost:8080/swagger-ui/index.html
    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .components(Components())
            .info(info())
    }

    private fun info(): Info {
        return Info()
            .title("Mongo Basic API")
            .description("Mongo API reference for developers")
            .version("1.0")
    }
}