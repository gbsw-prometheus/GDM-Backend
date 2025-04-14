package com.apple.team_prometheus.domain.going

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping



@RestController
@RequestMapping(value = ["/api/going"])
@Tag(name = "Going", description = "외출/외박 관련 API")
class GoingController(
    private val goingService: GoingService,
) {

     @GetMapping(value = ["/list"])
     @Operation(summary = "외출/외박 리스트 조회")
     fun getGoingList(): List<GoingDto.GoingListResponse> {
         return goingService.findAll()
     }

    @GetMapping(value = ["/{id}"])
    @Operation(summary = "외출/외박 상세 조회")
    fun getGoing(
        @PathVariable("id") goingId: Long
    ): GoingApply {

        return goingService.findById(goingId)
    }

    @PostMapping(value = ["/registration"])
    @Operation(summary = "외출/외박 신청")
    fun registrationGoing(
        goingRequest: GoingDto.GoingRequest
    ): GoingDto.GoingResponse {

        return goingService.registrationGoing(goingRequest)
    }

    @PostMapping(value = ["/accept/{id}"])
    @Operation(summary = "외출/외박 신청 수락")
    fun acceptGoingRequest(
        @PathVariable("id") goingId: Long
    ): GoingDto.GoingResponse {
        return goingService.acceptGoingRequest(goingId)
    }

    @DeleteMapping(value = ["/delete/{id}"])
    @Operation(summary = "외출/외박 신청 취소")
    fun deleteGoing(
        @PathVariable("id") goingId: Long
    ) {
        goingService.deleteGoing(goingId)
    }
}