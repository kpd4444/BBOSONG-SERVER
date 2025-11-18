package com.posong.ai_laundry.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberLoginResponse {
    private String token;
    private String loginId;
    private String name;
}