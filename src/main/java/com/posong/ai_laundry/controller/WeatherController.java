package com.posong.ai_laundry.controller;

import com.posong.ai_laundry.dto.WeatherResponse;
import com.posong.ai_laundry.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/weather")
public class WeatherController {

    private final WeatherService weatherService;

    /**
     * 날씨 정보 조회 API
     * 예: /api/weather/current?nx=60&ny=127
     */
    @GetMapping("/current")
    public ResponseEntity<WeatherResponse> getWeather(
            @RequestParam double nx,
            @RequestParam double ny
    ) throws Exception {
        WeatherResponse response = weatherService.getWeather(nx, ny);
        return ResponseEntity.ok(response);
    }
}
