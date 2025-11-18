package com.posong.ai_laundry.controller;

import com.posong.ai_laundry.dto.*;
import com.posong.ai_laundry.global.ApiResponse;
import com.posong.ai_laundry.member.Member;
import com.posong.ai_laundry.security.CustomUserDetails;
import com.posong.ai_laundry.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    // -----------------------------
    // 1. 회원가입
    // -----------------------------
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody MemberSignupRequest req) {
        memberService.signup(req);
        return ResponseEntity.ok("회원가입 성공");
    }

    // -----------------------------
    // 2. 로그인 (토큰 발급)
    // -----------------------------
    @PostMapping("/login")
    public ResponseEntity<MemberLoginResponse> login(@RequestBody MemberLoginRequest req) {

        return ResponseEntity.ok(memberService.login(req));
    }

    // -----------------------------
    // 3. 전체 회원 조회 (관리자만 쓴다고 가정 → 일단 인증 없음)
    // -----------------------------
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<?>> getAllMembers() {
        List<Member> list = memberService.findAll();
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/mypage")
    public ResponseEntity<ApiResponse<?>> myPage(@AuthenticationPrincipal CustomUserDetails user) {

        Member member = user.getMember();

        return ResponseEntity.ok(
                ApiResponse.success(new MyPageResponse(
                        member.getLoginId(),
                        member.getName(),
                        member.getEmail(),
                        member.getBirth()
                ))
        );
    }
    @PatchMapping("/mypage")
    public ResponseEntity<ApiResponse<?>> updateMyPage(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody MyPageUpdateRequest req
    ) {

        memberService.updateMyPage(user.getUsername(), req);

        return ResponseEntity.ok(ApiResponse.success("수정 완료"));
    }

}
