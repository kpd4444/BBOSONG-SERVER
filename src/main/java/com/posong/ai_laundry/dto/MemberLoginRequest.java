package com.posong.ai_laundry.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberLoginRequest {

    @NotEmpty
    private String loginId;

    @NotEmpty
    private String password;
}
