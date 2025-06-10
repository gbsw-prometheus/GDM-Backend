package com.apple.team_prometheus.domain.meal.dto


class MealDto {

    data class Response(
        val mealServiceDietInfo: List<MealInfo>
    )

    data class MealInfo(
        val row: List<Row>? = emptyList()
    )

    data class Row(
        val MLSV_YMD: String?,
        val MMEAL_SC_NM: String?,
        val DDISH_NM: String?
    )

}