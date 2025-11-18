package com.posong.ai_laundry.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WeatherResponse {
    private double temperature;
    private int humidity;
    private int rainProbability;
    private int sky;
    private int rainType;
    private double windSpeed;
    private WeatherAdvice advice; // ✅ 내부 필드로 추가
}
