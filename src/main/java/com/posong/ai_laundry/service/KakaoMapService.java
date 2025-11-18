package com.posong.ai_laundry.service;

import io.netty.channel.ChannelOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

// KakaoMapService.java
@Service
public class KakaoMapService {

    private final WebClient webClient;

    public KakaoMapService(
            @Value("${kakao.rest-api-key}") String apiKey) {

        this.webClient = WebClient.builder()
                .baseUrl("https://dapi.kakao.com")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "KakaoAK " + apiKey)
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create()
                                .responseTimeout(Duration.ofSeconds(5))
                                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)))
                .build();
    }

    public Mono<String> searchNearby(String query, double x, double y, int radius) {
        return webClient.get()
                .uri(uri -> uri.path("/v2/local/search/keyword.json")
                        .queryParam("query", query)
                        .queryParam("x", x)
                        .queryParam("y", y)
                        .queryParam("radius", radius)
                        .build(true)) // ✅ 한글 쿼리 인코딩
                .retrieve()
                .onStatus(HttpStatusCode::isError, res ->
                        res.bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .map(body -> new RuntimeException("Kakao API error " + res.statusCode() + " " + body)))
                .bodyToMono(String.class);
    }
}


