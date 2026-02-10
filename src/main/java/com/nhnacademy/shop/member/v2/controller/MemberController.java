package com.nhnacademy.shop.member.v2.controller;

import com.nhnacademy.shop.common.security.MemberDetail;
import com.nhnacademy.shop.member.v2.dto.MemberDetailResponse;
import com.nhnacademy.shop.member.v2.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RequestMapping("/members")
@RestController
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/me")
    public ResponseEntity<MemberDetailResponse> getMember(@AuthenticationPrincipal MemberDetail memberDetail) {
        Long memberId = memberDetail.getMemberId();
        MemberDetailResponse response = memberService.getMemberById(memberId);
        return ResponseEntity.ok(response);
    }


}
