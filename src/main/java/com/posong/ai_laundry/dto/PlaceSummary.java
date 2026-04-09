package com.posong.ai_laundry.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PlaceSummary {
    private String name;
    private String roadAddress;
    private String address;
    private String phone;
    private String placeUrl;
    private String category;
    private double latitude;
    private double longitude;
    private String distance;
}
