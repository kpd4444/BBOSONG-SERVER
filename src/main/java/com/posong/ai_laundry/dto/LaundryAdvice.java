package com.posong.ai_laundry.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LaundryAdvice {

    @JsonProperty("카테고리")
    @NotBlank
    private String category;        // 예: 상의, 아우터, 하의…

    @JsonProperty("소분류")
    @NotBlank
    private String subcategory;     // 예: 니트, 코트, 셔츠…

    @JsonProperty("소재")
    @NotBlank
    private String material;

    @JsonProperty("색상")
    @NotBlank
    private String color;

    @JsonProperty("세탁방법")
    @NotBlank
    private String washingMethod;

    @JsonProperty("주의사항")
    @NotBlank
    private String cautions;

    @JsonProperty("권장심볼")
    private List<String> recommendedSymbols;
}
