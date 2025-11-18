package com.posong.ai_laundry.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class MyPageResponse {
    private String loginId;
    private String name;
    private String email;
    private LocalDate birth; // LocalDate → 문자열로 리턴
}