package com.posong.ai_laundry.controller;

import com.posong.ai_laundry.service.KakaoMapService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/map")
@RequiredArgsConstructor
public class KakaoMapController {

    private final KakaoMapService kakao;

    @GetMapping("/laundry")
    public Mono<String> getNearby(
            @RequestParam String query,
            @RequestParam double x,
            @RequestParam double y,
            @RequestParam(defaultValue = "2000") int radius) {
        return kakao.searchNearby(query, x, y, radius);
    }
}


