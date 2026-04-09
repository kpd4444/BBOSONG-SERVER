package com.posong.ai_laundry.dto;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyPageUpdateRequest {
    private String name;

    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    private String birth;
}
