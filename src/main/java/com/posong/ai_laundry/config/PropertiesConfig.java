package com.posong.ai_laundry.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        AiProperties.class,
        ChatProperties.class,
        JwtProperties.class,
        KakaoProperties.class
})
public class PropertiesConfig {
}
