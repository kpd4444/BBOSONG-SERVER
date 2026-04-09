package com.posong.ai_laundry.config;

import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.chat")
public record ChatProperties(
        @Min(1) int historyLimit,
        @Min(1) int retentionLimit
) {
}
