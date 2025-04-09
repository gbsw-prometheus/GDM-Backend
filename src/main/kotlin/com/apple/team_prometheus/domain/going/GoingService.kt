package com.apple.team_prometheus.domain.going

import com.apple.team_prometheus.domain.auth.AuthRepository
import com.apple.team_prometheus.domain.auth.AuthUser
import com.apple.team_prometheus.global.exception.ErrorCode
import com.apple.team_prometheus.global.exception.Exceptions
import org.springframework.stereotype.Service


@Service
class GoingService(
    private val goingRepository: GoingRepository,
    private val authRepository: AuthRepository,
) {

    fun findAll(): List<GoingDto.GoingListResponse> {
        return goingRepository.findAll().map {
            GoingDto.GoingListResponse(
                id = it.id,
                user = it.user,
                going = it.going
            )
        }
    }

    fun registrationGoing(goingRequest: GoingDto.GoingRequest): GoingDto.GoingResponse {
        if (goingRequest.outDateTime.isBefore(java.time.LocalDate.now()) ||
            goingRequest.inDateTime.isBefore(java.time.LocalDate.now()) ||
            goingRequest.outDateTime.isBefore(goingRequest.inDateTime)) {

            throw Exceptions(
                ErrorCode.INVALID_DATE_ERROR
            )
        }

        val student: AuthUser? = authRepository.findById(goingRequest.userId)
            .orElseThrow {
                Exceptions(
                    ErrorCode.USER_NOT_FOUND
                )
            }

        val goingApply: GoingApply = GoingApply(
            user = student!!,
            outDateTime = goingRequest.outDateTime,
            inDateTime = goingRequest.inDateTime,
            title = goingRequest.title,
            content = goingRequest.content
        )

        val saveGoingApply: GoingApply = goingRepository.save(goingApply)

        val response: GoingDto.GoingResponse = GoingDto.GoingResponse(
            id = saveGoingApply.id,
            userId = saveGoingApply.user,
            going = saveGoingApply.going
        )

        return response
    }


    fun acceptGoingRequest(goingId: Long): GoingDto.GoingResponse {
        val goingApply = goingRepository.findById(goingId).orElseThrow {
            Exceptions(
                ErrorCode.INVALID_REQUEST_ERROR
            )
        }

        // 신청 수락 처리 (예: 상태 업데이트)
        goingApply.going = true
        val updatedGoingApply = goingRepository.save(goingApply)

        return GoingDto.GoingResponse(
            id = updatedGoingApply.id,
            userId = updatedGoingApply.user,
            going = updatedGoingApply.going
        )
    }
}