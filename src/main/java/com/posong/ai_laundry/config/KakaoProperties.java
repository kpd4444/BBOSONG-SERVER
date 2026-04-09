package com.posong.ai_laundry.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.kakao")
public record KakaoProperties(
        @NotBlank String restKey
) {
}
