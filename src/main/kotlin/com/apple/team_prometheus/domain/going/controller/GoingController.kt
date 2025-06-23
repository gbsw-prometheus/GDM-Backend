package com.apple.team_prometheus.domain.going.controller

import com.apple.team_prometheus.domain.going.dto.GoingDto
import com.apple.team_prometheus.domain.going.service.GoingService
import com.apple.team_prometheus.domain.going.entity.GoingApply
import io.jsonwebtoken.Jwt
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.User
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
     fun getGoingList(): List<GoingDto.ListResponse> {
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
        request: GoingDto.Request,
        authentication: Authentication
    ): GoingDto.Response {

        request.userBirth = authentication.name.split(" ")[0]
        request.userName = authentication.name.split(" ")[1]

        return goingService.registrationGoing(request)
    }

    @PostMapping(value = ["/accept/{id}"])
    @Operation(summary = "외출/외박 신청 수락")
    fun acceptGoingRequest(
        @PathVariable("id") goingId: Long
    ): GoingDto.Response {
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