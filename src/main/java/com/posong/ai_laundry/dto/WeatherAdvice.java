package com.posong.ai_laundry.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class WeatherAdvice {
    private String summary;           // 대표 문장 (1줄)
    private List<String> adviceList;  // 세부 조언 (최대 2개)
}