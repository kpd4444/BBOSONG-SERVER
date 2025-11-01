package com.posong.ai_laundry.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/places")
public class PlaceController {

    @Value("${kakao.rest-key}")
    private String kakaoRestKey;

    private RestClient kakaoClient() {
        return RestClient.builder()
                .baseUrl("https://dapi.kakao.com")
                .defaultHeader("Authorization", "KakaoAK " + kakaoRestKey)
                .build();
    }

    @GetMapping("/search")
    public ResponseEntity<String> search(
            @RequestParam String query,
            @RequestParam double y,
            @RequestParam double x,
            @RequestParam(defaultValue = "1500") Integer radius) {

        final int r = (radius == null || radius <= 0) ? 1500 : radius;

        String body = kakaoClient().get()
                .uri(u -> u.scheme("https")
                        .host("dapi.kakao.com")
                        .path("/v2/local/search/keyword.json")
                        .queryParam("query", query)
                        .queryParam("y", y)
                        .queryParam("x", x)
                        .queryParam("radius", r)
                        .build())
                .retrieve()
                .toEntity(String.class)
                .getBody();

        return ResponseEntity.ok(body);
    }
}