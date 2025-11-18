package com.posong.ai_laundry.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyPageUpdateRequest {
    private String name;
    private String email;
    private String birth;
}