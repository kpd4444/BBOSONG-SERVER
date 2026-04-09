package com.posong.ai_laundry.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberAvailabilityResponse {
    private String value;
    private boolean available;
}
