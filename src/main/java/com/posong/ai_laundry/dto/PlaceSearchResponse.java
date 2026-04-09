package com.posong.ai_laundry.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PlaceSearchResponse {
    private int totalCount;
    private boolean end;
    private List<PlaceSummary> places;
}
