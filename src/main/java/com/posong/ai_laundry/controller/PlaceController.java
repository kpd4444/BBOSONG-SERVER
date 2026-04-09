package com.posong.ai_laundry.controller;

import com.posong.ai_laundry.dto.PlaceSearchResponse;
import com.posong.ai_laundry.global.ApiResponse;
import com.posong.ai_laundry.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/places")
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceService placeService;

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PlaceSearchResponse>> search(
            @RequestParam String query,
            @RequestParam double y,
            @RequestParam double x,
            @RequestParam(defaultValue = "1500") Integer radius) {

        return ResponseEntity.ok(ApiResponse.success(placeService.search(query, y, x, radius)));
    }
}
