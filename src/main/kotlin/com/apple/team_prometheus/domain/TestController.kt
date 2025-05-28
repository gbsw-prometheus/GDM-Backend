package com.apple.team_prometheus.domain

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class TestController {


    @GetMapping("/health")
    fun healthCheck(): String {
        return "유진승\n유진승\n유진승\n유진승"
    }

}