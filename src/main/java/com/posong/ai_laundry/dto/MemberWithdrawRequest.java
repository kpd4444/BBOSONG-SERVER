package com.posong.ai_laundry.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberWithdrawRequest {

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;
}
