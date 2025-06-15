package com.apple.team_prometheus.domain.meal.service

import com.apple.team_prometheus.domain.meal.dto.MealDto
import com.apple.team_prometheus.global.exception.ErrorCode
import com.apple.team_prometheus.global.exception.Exceptions
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriBuilder
import org.threeten.bp.LocalDate
import reactor.core.publisher.Mono


@Service
class MealService(
    webClientBuilder: WebClient.Builder
) {

    val webClient: WebClient = webClientBuilder
        .baseUrl("https://open.neis.go.kr")
        .build()

    @Value("\${meal.api.key}")
    private final lateinit var API_KEY: String
    private final val ATPT_OFCDC_SC_CODE = "R10"
    private final val SD_SCHUL_CODE = "8750829"

    fun getMealData(): MealDto.Response {
        val date = LocalDate.now().toString().replace("-", "")

        val response = webClient.get()
            .uri { uriBuilder: UriBuilder ->
                uriBuilder
                    .path("/hub/mealServiceDietInfo")
                    .queryParam("KEY", API_KEY)
                    .queryParam("Type", "json")
                    .queryParam("ATPT_OFCDC_SC_CODE", ATPT_OFCDC_SC_CODE)
                    .queryParam("SD_SCHUL_CODE", SD_SCHUL_CODE)
                    .queryParam("MLSV_YMD", date)
                    .build()
            }
            .retrieve()
            .onStatus({ it.isError }) { res ->
                res.bodyToMono(String::class.java)
                    .flatMap {
                        Mono.error(
                            RuntimeException("API 호출 실패: ${res.statusCode()}, body: $it")
                        )
                    }
            }
            .bodyToMono(MealDto.Response::class.java)
            .switchIfEmpty(
                Mono.error(
                    Exceptions(errorCode = ErrorCode.MEAL_NOT_FOUND)
                )
            )
            .block() ?: throw Exceptions(errorCode = ErrorCode.MEAL_NOT_FOUND)

        val filteredInfo = response.mealServiceDietInfo?.filter {
            !it.row.isNullOrEmpty()
        }

        if (filteredInfo != null) {
            if (filteredInfo.isEmpty()) {
                throw Exceptions(
                    errorCode = ErrorCode.MEAL_NOT_FOUND
                )
            }
        } else {
            throw Exceptions(
                errorCode = ErrorCode.MEAL_NOT_FOUND
            )
        }

        return MealDto.Response(
            mealServiceDietInfo = filteredInfo
        )
    }

}