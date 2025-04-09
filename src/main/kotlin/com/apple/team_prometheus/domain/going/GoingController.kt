package com.apple.team_prometheus.domain.going

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController


@RestController(value = "/api/going")
class GoingController(
    private val goingService: GoingService,
) {

     @GetMapping(value = ["/list"])
     fun getGoingList(): List<GoingDto.GoingListResponse> {
         return goingService.findAll()
     }

    @PostMapping(value = ["/registration"])
    fun registrationGoing(
        goingRequest: GoingDto.GoingRequest
    ): GoingDto.GoingResponse {

        return goingService.registrationGoing(goingRequest)
    }

    @PostMapping(value = ["/accept/{id}"])
    fun acceptGoingRequest(
        @PathVariable("id") goingId: Long
    ): GoingDto.GoingResponse {
        return goingService.acceptGoingRequest(goingId)
    }
}