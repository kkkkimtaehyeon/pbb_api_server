package com.nhnacademy.shop.admin.service;

import com.nhnacademy.shop.member.v2.dto.MemberDetailResponse;
import com.nhnacademy.shop.member.v2.entity.Member;
import com.nhnacademy.shop.member.v2.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberAdminService {
    private final MemberRepository memberRepository;

    public Page<MemberDetailResponse> getMembers(Pageable pageable) {
        Page<Member> members = memberRepository.findAll(pageable);
        return members.map(member -> MemberDetailResponse.builder()
                .id(member.getId())
                .grade(member.getMemberGrade().getDisplayName())
                .name(member.getName())
                .role(member.getRole().toString())
                .email(member.getEmail())
                .build()
        );
    }
}
