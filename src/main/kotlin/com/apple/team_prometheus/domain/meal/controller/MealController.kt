package com.apple.team_prometheus.domain.meal.controller

import com.apple.team_prometheus.domain.meal.dto.MealDto
import com.apple.team_prometheus.domain.meal.service.MealService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping(value = ["/api/meals"])
class MealController(
    private val mealService: MealService
) {


    @GetMapping(value = ["/daily"])
    fun getMeal(): ResponseEntity<MealDto.Response> {

        val mealData = mealService.getMealData()

        return ResponseEntity.ok(mealData)
    }
}