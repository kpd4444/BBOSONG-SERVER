package com.posong.ai_laundry.service;


import com.posong.ai_laundry.dto.*;
import com.posong.ai_laundry.member.Member;
import com.posong.ai_laundry.member.MemberRepository;
import com.posong.ai_laundry.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;

    // ----------------------
    // 회원가입
    // ----------------------
    public void signup(MemberSignupRequest req) {

        if (memberRepository.existsByLoginId(req.getLoginId())) {
            throw new RuntimeException("이미 존재하는 아이디입니다.");
        }

        if (memberRepository.existsByEmail(req.getEmail())) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }

        Member member = new Member();
        member.setLoginId(req.getLoginId());
        member.setEmail(req.getEmail());
        member.setName(req.getName());
        member.setPassword(req.getPassword());


        memberRepository.save(member);
    }

    // ----------------------
    // 로그인 (토큰 발급)
    // ----------------------
    public MemberLoginResponse login(MemberLoginRequest req) {

        Member member = memberRepository.findByLoginId(req.getLoginId())
                .orElseThrow(() -> new RuntimeException("아이디 또는 비밀번호가 잘못되었습니다."));

        if (!member.getPassword().equals(req.getPassword())) {
            throw new RuntimeException("아이디 또는 비밀번호가 잘못되었습니다.");
        }

        // 🔥 JWT 발급
        String token = jwtUtil.generateToken(member.getLoginId());

        return new MemberLoginResponse(
                token,
                member.getLoginId(),
                member.getName()
        );
    }

    // ----------------------
    // 로그인 ID 기준으로 회원 조회
    // ----------------------
    public Member findByLoginId(String loginId) {
        return memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("회원이 없습니다."));
    }

    // ----------------------
    // 전체 회원 조회
    // ----------------------
    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    public void updateMyPage(String loginId, MyPageUpdateRequest req) {

        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));

        // 수정 가능한 항목만 업데이트
        if (req.getName() != null && !req.getName().isBlank()) {
            member.setName(req.getName());
        }

        if (req.getEmail() != null && !req.getEmail().isBlank()) {
            member.setEmail(req.getEmail());
        }

        if (req.getBirth() != null && !req.getBirth().isBlank()) {
            member.setBirth(LocalDate.parse(req.getBirth()));
        }

        memberRepository.save(member);
    }

}
