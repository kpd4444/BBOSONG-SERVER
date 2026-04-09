package com.posong.ai_laundry.service;


import com.posong.ai_laundry.dto.*;
import com.posong.ai_laundry.global.CustomException;
import com.posong.ai_laundry.global.ErrorCode;
import com.posong.ai_laundry.member.Member;
import com.posong.ai_laundry.member.MemberRepository;
import com.posong.ai_laundry.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    // ----------------------
    // 회원가입
    // ----------------------
    @Transactional
    public void signup(MemberSignupRequest req) {

        if (memberRepository.existsByLoginId(req.getLoginId())) {
            throw new CustomException(ErrorCode.DUPLICATE_LOGIN_ID);
        }

        if (memberRepository.existsByEmail(req.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        Member member = new Member();
        member.setLoginId(req.getLoginId());
        member.setEmail(req.getEmail());
        member.setName(req.getName());
        // 비밀번호 암호화 저장
        member.setPassword(passwordEncoder.encode(req.getPassword()));


        memberRepository.save(member);
    }

    // ----------------------
    // 로그인 (토큰 발급)
    // ----------------------
    @Transactional
    public MemberLoginResponse login(MemberLoginRequest req) {

        Member member = memberRepository.findByLoginId(req.getLoginId())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_CREDENTIALS));

        // 암호화된 비밀번호 비교
        if (!passwordEncoder.matches(req.getPassword(), member.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
        }

        // JWT 발급
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
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }

    // ----------------------
    // 전체 회원 조회
    // ----------------------
    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    @Transactional
    public void updateMyPage(String loginId, MyPageUpdateRequest req) {

        Member member = findByLoginId(loginId);

        // 수정 가능한 항목만 업데이트
        if (req.getName() != null && !req.getName().isBlank()) {
            member.setName(req.getName());
        }

        if (req.getEmail() != null && !req.getEmail().isBlank()) {
            if (!req.getEmail().equals(member.getEmail()) && memberRepository.existsByEmail(req.getEmail())) {
                throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
            }
            member.setEmail(req.getEmail());
        }

        if (req.getBirth() != null && !req.getBirth().isBlank()) {
            member.setBirth(LocalDate.parse(req.getBirth()));
        }

        memberRepository.save(member);
    }

    public MemberAvailabilityResponse checkLoginIdAvailability(String loginId) {
        return new MemberAvailabilityResponse(loginId, !memberRepository.existsByLoginId(loginId));
    }

    public MemberAvailabilityResponse checkEmailAvailability(String email) {
        return new MemberAvailabilityResponse(email, !memberRepository.existsByEmail(email));
    }

    @Transactional
    public void changePassword(String loginId, MemberPasswordChangeRequest req) {
        Member member = findByLoginId(loginId);

        if (!passwordEncoder.matches(req.getCurrentPassword(), member.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        if (passwordEncoder.matches(req.getNewPassword(), member.getPassword())) {
            throw new CustomException(ErrorCode.SAME_AS_OLD_PASSWORD);
        }

        member.setPassword(passwordEncoder.encode(req.getNewPassword()));
        memberRepository.save(member);
    }

    @Transactional
    public void withdraw(String loginId, MemberWithdrawRequest req) {
        Member member = findByLoginId(loginId);

        if (!passwordEncoder.matches(req.getPassword(), member.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        memberRepository.delete(member);
    }

}
