package com.nhnacademy.shop.member.v2.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
public class MemberDetailResponse {
    private Long id;
    private String email;
    private String name;
    private String grade;
    private String role;

    @Builder
    public MemberDetailResponse(Long id, String email, String name, String grade, String role) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.grade = grade;
        this.role = role;
    }
}
