package com.apple.team_prometheus.domain.going.service

import com.apple.team_prometheus.domain.auth.repository.AuthRepository
import com.apple.team_prometheus.domain.auth.entity.AuthUser
import com.apple.team_prometheus.domain.going.repository.GoingRepository
import com.apple.team_prometheus.domain.going.dto.GoingDto
import com.apple.team_prometheus.domain.going.entity.GoingApply
import com.apple.team_prometheus.global.exception.ErrorCode
import com.apple.team_prometheus.global.exception.Exceptions
import org.springframework.stereotype.Service
import java.time.LocalDate


@Service
class GoingService(
    private val goingRepository: GoingRepository,
    private val authRepository: AuthRepository,
) {

    fun findAll(): List<GoingDto.ListResponse> {
        return goingRepository.findAll().map {
            GoingDto.ListResponse(
                id = it.id,
                user = it.user,
                going = it.going
            )
        }
    }

    fun findById(id: Long): GoingApply {
        val goingApply = goingRepository.findById(id).orElseThrow {
            Exceptions(
                ErrorCode.GOING_NOT_FOUND
            )
        }

        return goingApply.let {
            GoingApply(
                user = it.user,
                going = it.going,
                outDateTime = it.outDateTime,
                inDateTime = it.inDateTime,
                title = it.title,
                content = it.content
            )
        }
    }

    fun deleteGoing(id: Long) {
        val goingApply = goingRepository.findById(id).orElseThrow {
            Exceptions(
                ErrorCode.GOING_NOT_FOUND
            )
        }

        goingRepository.delete(goingApply)
    }

    fun registrationGoing(request: GoingDto.Request): GoingDto.Response {
        if (request.outDateTime.isBefore(LocalDate.now()) ||
            request.inDateTime.isBefore(LocalDate.now()) ||
            request.outDateTime.isBefore(request.inDateTime)) {

            throw Exceptions(
                ErrorCode.INVALID_DATE_ERROR
            )
        }

        val student: AuthUser? = request.userName?.let {
            authRepository.findByBirthYearAndName(
                birth = LocalDate.parse(request.userBirth),
                name = it
            )
                .orElseThrow {
                    Exceptions(
                        ErrorCode.USER_NOT_FOUND
                    )
                }
        }

        val goingApply: GoingApply = GoingApply(
            user = student!!,
            outDateTime = request.outDateTime,
            inDateTime = request.inDateTime,
            title = request.title,
            content = request.content
        )

        val saveGoingApply: GoingApply = goingRepository.save(goingApply)

        val response: GoingDto.Response = GoingDto.Response(
            id = saveGoingApply.id,
            userId = saveGoingApply.user,
            going = saveGoingApply.going
        )

        return response
    }


    fun acceptGoingRequest(goingId: Long): GoingDto.Response {
        val goingApply = goingRepository.findById(goingId).orElseThrow {
            Exceptions(
                ErrorCode.INVALID_REQUEST_ERROR
            )
        }

        // 신청 수락 처리 (예: 상태 업데이트)
        goingApply.going = true
        val updatedGoingApply = goingRepository.save(goingApply)

        return GoingDto.Response(
            id = updatedGoingApply.id,
            userId = updatedGoingApply.user,
            going = updatedGoingApply.going
        )
    }
}