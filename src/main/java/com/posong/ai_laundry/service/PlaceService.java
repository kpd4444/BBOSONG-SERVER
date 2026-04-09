package com.posong.ai_laundry.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.posong.ai_laundry.config.KakaoProperties;
import com.posong.ai_laundry.dto.PlaceSearchResponse;
import com.posong.ai_laundry.dto.PlaceSummary;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;

@Service
public class PlaceService {

    private final RestClient kakaoClient;

    public PlaceService(KakaoProperties kakaoProperties) {
        this.kakaoClient = RestClient.builder()
                .baseUrl("https://dapi.kakao.com")
                .defaultHeader("Authorization", "KakaoAK " + kakaoProperties.restKey())
                .build();
    }

    public PlaceSearchResponse search(String query, double y, double x, Integer radius) {
        int validRadius = (radius == null || radius <= 0) ? 1500 : radius;

        KakaoPlaceSearchResponse response = kakaoClient.get()
                .uri(u -> u.scheme("https")
                        .host("dapi.kakao.com")
                        .path("/v2/local/search/keyword.json")
                        .queryParam("query", query)
                        .queryParam("y", y)
                        .queryParam("x", x)
                        .queryParam("radius", validRadius)
                        .build())
                .retrieve()
                .body(KakaoPlaceSearchResponse.class);

        if (response == null) {
            return new PlaceSearchResponse(0, true, Collections.emptyList());
        }

        List<PlaceSummary> places = response.documents() == null
                ? Collections.emptyList()
                : response.documents().stream()
                .map(this::toPlaceSummary)
                .toList();

        int totalCount = response.meta() == null ? 0 : response.meta().totalCount();
        boolean end = response.meta() == null || response.meta().isEnd();

        return new PlaceSearchResponse(totalCount, end, places);
    }

    private PlaceSummary toPlaceSummary(KakaoPlaceDocument document) {
        return new PlaceSummary(
                document.placeName(),
                document.roadAddressName(),
                document.addressName(),
                document.phone(),
                document.placeUrl(),
                document.categoryName(),
                parseCoordinate(document.y()),
                parseCoordinate(document.x()),
                document.distance()
        );
    }

    private double parseCoordinate(String value) {
        return value == null || value.isBlank() ? 0.0 : Double.parseDouble(value);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record KakaoPlaceSearchResponse(
            KakaoMeta meta,
            List<KakaoPlaceDocument> documents
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record KakaoMeta(
            @JsonProperty("total_count") int totalCount,
            @JsonProperty("is_end") boolean isEnd
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record KakaoPlaceDocument(
            @JsonProperty("place_name") String placeName,
            @JsonProperty("road_address_name") String roadAddressName,
            @JsonProperty("address_name") String addressName,
            String phone,
            @JsonProperty("place_url") String placeUrl,
            @JsonProperty("category_name") String categoryName,
            String x,
            String y,
            String distance
    ) {
    }
}
