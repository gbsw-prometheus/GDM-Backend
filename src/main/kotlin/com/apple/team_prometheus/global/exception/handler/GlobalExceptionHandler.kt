package com.apple.team_prometheus.global.exception.handler

import com.apple.team_prometheus.domain.attendance.controller.AttendanceController
import com.apple.team_prometheus.domain.auth.controller.AuthController
import com.apple.team_prometheus.domain.going.controller.GoingController
import com.apple.team_prometheus.domain.meal.controller.MealController
import com.apple.team_prometheus.domain.notification.controller.NotificationController
import com.apple.team_prometheus.global.exception.Exceptions
import com.apple.team_prometheus.global.exception.dto.ErrorMessage
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RestControllerAdvice


@RestControllerAdvice(
    annotations = [RestController::class],
    basePackageClasses = [AuthController::class, MealController::class, NotificationController::class,
        AttendanceController::class, GoingController::class]
)
class GlobalExceptionHandler{

    @ExceptionHandler(Exceptions::class)
    fun handleCustomExceptions(e: Exceptions): ResponseEntity<ErrorMessage.ErrorResponse> {
        return ResponseEntity
            .status(e.errorCode.httpStatus)
            .body(
                ErrorMessage.ErrorResponse(
                    status = e.errorCode.httpStatus.value(),
                    message = e.errorCode.errorMessage
                )
            )
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleBadRequest(e: IllegalArgumentException): ResponseEntity<ErrorMessage.ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ErrorMessage.ErrorResponse(
                    status = HttpStatus.BAD_REQUEST.value(),
                    message = e.message ?: "잘못된 요청입니다."
                )
            )
    }

    @ExceptionHandler(RuntimeException::class)
    fun handleInternalServerError(e: RuntimeException): ResponseEntity<ErrorMessage.ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(
                ErrorMessage.ErrorResponse(
                    status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    message = e.message ?: "서버 오류가 발생했습니다."
                )
            )
    }
}
