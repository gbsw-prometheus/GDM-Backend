package com.apple.team_prometheus.domain.meal.dto

import io.swagger.v3.oas.annotations.media.Schema


class MealDto {

    @Schema(name = "MealResponse")
    data class Response(
        val mealServiceDietInfo: List<MealInfo>?
    )

    @Schema(name = "MealInfo")
    data class MealInfo(
        val head: List<Map<String, Any>>? = null,
        val row: List<Row>? = emptyList()
    )

    @Schema(name = "Row")
    data class Row(
        val MLSV_YMD: String?,
        val MMEAL_SC_NM: String?,
        val DDISH_NM: String?
    )

}