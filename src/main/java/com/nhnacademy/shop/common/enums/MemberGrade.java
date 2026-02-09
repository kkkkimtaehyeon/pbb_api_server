package com.nhnacademy.shop.common.enums;

import lombok.Getter;

@Getter
public enum MemberGrade {
    BRONZE("브론즈"),
    SILVER("실버"),
    GOLD("골드"),
    DIAMOND("다이아몬드");

    private final String displayName;

    MemberGrade(String displayName) {
        this.displayName = displayName;
    }
}
