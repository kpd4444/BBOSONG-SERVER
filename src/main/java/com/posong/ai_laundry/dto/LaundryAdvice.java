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

    @JsonProperty("소재")
    @NotBlank
    private String material;          // e.g., "면 100%", "울 혼방"

    @JsonProperty("색상")
    @NotBlank
    private String color;             // e.g., "화이트", "차콜 그레이"

    @JsonProperty("세탁방법")
    @NotBlank
    private String washingMethod;     // 온도/세제/코스/건조 요약

    @JsonProperty("주의사항")
    @NotBlank
    private String cautions;          // 건조기 금지, 뒤집어 세탁망 등

    @JsonProperty("권장심볼")
    private List<String> recommendedSymbols; // 예: ["DoNotBleach","DryFlat"]
}
