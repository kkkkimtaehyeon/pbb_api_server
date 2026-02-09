package com.nhnacademy.shop.admin.controller;

import com.nhnacademy.shop.admin.service.MemberAdminService;
import com.nhnacademy.shop.member.v2.dto.MemberDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/admin/members")
@RestController
public class MemberAdminController {
    private final MemberAdminService memberAdminService;

    @GetMapping
    public ResponseEntity<Page<MemberDetailResponse>> getMembers(Pageable pageable) {
        Page<MemberDetailResponse> members = memberAdminService.getMembers(pageable);
        return ResponseEntity.ok(members);
    }
}
