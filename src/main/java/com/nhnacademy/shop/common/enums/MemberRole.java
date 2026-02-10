package com.nhnacademy.shop.common.enums;

import lombok.Getter;

@Getter
public enum MemberRole {
    ROLE_MEMBER, // 일반 사용자
    ROLE_TENANT, // 개인 판매 관리자
    ROLE_OPERATOR, // cs 담당자
    ROLE_ADMIN, // 서비스 전체 관리자
}
