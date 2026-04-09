package com.posong.ai_laundry.service;

import com.posong.ai_laundry.dto.MemberLoginRequest;
import com.posong.ai_laundry.dto.MemberPasswordChangeRequest;
import com.posong.ai_laundry.dto.MemberSignupRequest;
import com.posong.ai_laundry.dto.MemberWithdrawRequest;
import com.posong.ai_laundry.global.CustomException;
import com.posong.ai_laundry.member.Member;
import com.posong.ai_laundry.member.MemberRepository;
import com.posong.ai_laundry.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private MemberRepository memberRepository;

    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberService memberService;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        memberService = new MemberService(jwtUtil, memberRepository, passwordEncoder);
    }

    @Test
    void signupEncryptsPasswordBeforeSaving() {
        MemberSignupRequest req = new MemberSignupRequest();
        req.setLoginId("tester01");
        req.setName("tester");
        req.setEmail("tester@example.com");
        req.setPassword("password123");

        when(memberRepository.existsByLoginId(req.getLoginId())).thenReturn(false);
        when(memberRepository.existsByEmail(req.getEmail())).thenReturn(false);

        memberService.signup(req);

        ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);
        verify(memberRepository).save(captor.capture());
        assertThat(captor.getValue().getPassword()).isNotEqualTo(req.getPassword());
        assertThat(passwordEncoder.matches(req.getPassword(), captor.getValue().getPassword())).isTrue();
    }

    @Test
    void loginReturnsTokenWhenPasswordMatches() {
        Member member = new Member();
        member.setLoginId("tester01");
        member.setName("tester");
        member.setPassword(passwordEncoder.encode("password123"));

        MemberLoginRequest req = new MemberLoginRequest();
        req.setLoginId("tester01");
        req.setPassword("password123");

        when(memberRepository.findByLoginId("tester01")).thenReturn(Optional.of(member));
        when(jwtUtil.generateToken("tester01")).thenReturn("jwt-token");

        var response = memberService.login(req);

        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getLoginId()).isEqualTo("tester01");
    }

    @Test
    void changePasswordRejectsSamePassword() {
        Member member = new Member();
        member.setLoginId("tester01");
        member.setPassword(passwordEncoder.encode("password123"));

        MemberPasswordChangeRequest req = new MemberPasswordChangeRequest();
        req.setCurrentPassword("password123");
        req.setNewPassword("password123");

        when(memberRepository.findByLoginId("tester01")).thenReturn(Optional.of(member));

        assertThatThrownBy(() -> memberService.changePassword("tester01", req))
                .isInstanceOf(CustomException.class);

        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    void withdrawDeletesMemberWhenPasswordMatches() {
        Member member = new Member();
        member.setLoginId("tester01");
        member.setPassword(passwordEncoder.encode("password123"));

        MemberWithdrawRequest req = new MemberWithdrawRequest();
        req.setPassword("password123");

        when(memberRepository.findByLoginId("tester01")).thenReturn(Optional.of(member));

        memberService.withdraw("tester01", req);

        verify(memberRepository).delete(member);
    }
}
