package com.nhnacademy.shop.member.v2.service;


import com.nhnacademy.shop.common.enums.MemberGrade;
import com.nhnacademy.shop.common.enums.MemberRole;
import com.nhnacademy.shop.common.exceptions.SignupFailedException;
import com.nhnacademy.shop.member.v2.dto.MemberDetailResponse;
import com.nhnacademy.shop.member.v2.dto.MemberRegisterRequest;
import com.nhnacademy.shop.member.v2.entity.Member;
import com.nhnacademy.shop.member.v2.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Member validateMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원정보가 존재하지 않습니다."));
    }

    @Transactional(readOnly = true)
    public Member validateAdmin(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원정보가 존재하지 않습니다."));
        if (!member.isAdmin()) {
            throw new IllegalArgumentException("관리자 권한이 없는 회원입니다.");
        }
        return member;
    }

    @Transactional(readOnly = true)
    public Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("회원정보가 존재하지 않습니다."));

    }

    @Transactional
    public Long register(MemberRegisterRequest request) {
        // 1. 이메일 중복 체크 (선택 사항이지만 권장)
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new SignupFailedException("이미 존재하는 이메일입니다.");
        }

        // 2. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 3. DTO -> Entity 변환
        Member member = Member.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(encodedPassword) // 암호화된 비밀번호 저장
                .memberGrade(MemberGrade.BRONZE)
                .role(MemberRole.ROLE_MEMBER)
                .build();

        // 4. 저장 및 ID 반환
        return memberRepository.save(member).getId();
    }

    @Transactional(readOnly = true)
    public MemberDetailResponse getMemberById(Long memberId) {
        Member member = validateMember(memberId);
        return MemberDetailResponse.builder()
//                .id(memberId)
                .email(member.getEmail())
                .name(member.getName())
                .grade(member.getMemberGrade().getDisplayName())
                .role(member.getRole().toString())
                .build();
    }
}
