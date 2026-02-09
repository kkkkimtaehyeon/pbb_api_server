package com.nhnacademy.shop.common.enums;

import lombok.Getter;

@Getter
public enum OrderClaimStatus {

    REQUESTED,        // 고객 요청
    CONFIRMED,         // 관리자 승인

    IN_PROGRESS,      // 처리 중 (회수/검수/환불 대기 등)

    COMPLETED,        // 처리 완료
    REJECTED,         // 거절

    CANCELED,          // 요청 철회 (고객이 취소)
    FAILED,              // 실패 (PG 오류, 재고 부족 등)

    REFUND_PROGRESSING,   // 환불 처리중 (내부 상태, 선택적)
    REFUNDED
}
