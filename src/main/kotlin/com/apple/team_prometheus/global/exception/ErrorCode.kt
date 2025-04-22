package com.apple.team_prometheus.global.exception

import org.springframework.http.HttpStatus


enum class ErrorCode(val httpStatus: HttpStatus, val errorMessage: String) {
    // Status 400
    INVALID_REQUEST_ERROR(HttpStatus.BAD_REQUEST, "잘못된 요청값"),
    INVALID_DATE_ERROR(HttpStatus.BAD_REQUEST, "잘못된 날짜값"),
    ALREADY_ATTENDED(HttpStatus.BAD_REQUEST, "이미 출석 체크한 유저입니다."),

    // Status 401
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰값"),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "틀린 비밀번호"),


    // Status 403
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한 없음!"),
    NOT_ATTENDANCE_TIME(HttpStatus.FORBIDDEN, "현재 출석시간 아님"),


    // Status 404
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "유저를 찾을 수 없음"),
    GOING_NOT_FOUND(HttpStatus.NOT_FOUND, "신청한 유저를 찾을 수 없음"),

    // Status 409
    DUPLICATED(HttpStatus.CONFLICT, "이미 있는 유저 정보"),

    // Status 500
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류")

}