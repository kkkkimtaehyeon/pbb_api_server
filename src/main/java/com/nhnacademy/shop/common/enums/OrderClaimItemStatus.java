package com.nhnacademy.shop.common.enums;

import lombok.Getter;

@Getter
public enum OrderClaimItemStatus {
    REQUESTED,     // 클레임 요청
    APPROVED,      // 승인
    REJECTED,      // 거절
    COLLECTING,    // 수거중
    COLLECTED,     // 수거완료
    INSPECTING,    // 검수중
    COMPLETED      // 환불/처리 완료
}
