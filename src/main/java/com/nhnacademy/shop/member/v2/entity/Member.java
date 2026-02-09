package com.nhnacademy.shop.member.v2.entity;

import com.nhnacademy.shop.common.enums.MemberGrade;
import com.nhnacademy.shop.common.enums.MemberRole;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MemberGrade memberGrade;

    //이름
    @Column(nullable = false, length = 100)
    private String name;

    //이메일
    @Column(nullable = false, length = 100, unique = true)
    private String email;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MemberRole role;

    @Builder
    public Member(Long id, MemberGrade memberGrade, String name, String email, String password, MemberRole role) {
        this.id = id;
        this.memberGrade = memberGrade;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public boolean isAdmin() {
        return role == MemberRole.ROLE_ADMIN;
    }
}
