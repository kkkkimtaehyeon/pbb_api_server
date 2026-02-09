package com.nhnacademy.shop.common.enums;
import lombok.Getter;


// 나중에 로케일을 사용한 국제화
@Getter
public enum OrderItemStatus {
    PAYMENT_PENDING("PAYMENT_PENDING", "결제대기"),
    PAYMENT_COMPLETED("PAYMENT_COMPLETED", "결제완료"),

    SHIPPED("SHIPPED", "발송완료"),
    DELIVERING("DELIVERING", "배송중"),
    DELIVERED("DELIVERED", "배송완료"),

    ORDER_CANCELLED("ORDER_CANCELLED", "주문취소"),
    RETURN_REQUESTED("RETURN_REQUESTED", "반품요청"),
    RETURN_COMPLETED("RETURN_COMPLETED", "반품완료"),

    PURCHASE_CONFIRMED("PURCHASE_CONFIRM", "구매확정");

    private final String status;
    private final String displayName;

    OrderItemStatus(String status, String displayName) {
        this.status = status;
        this.displayName = displayName;
    }

    public boolean isCancellable() {
        // 결제 전이거나 결제 완료일때만 주문취소 가능
        return status.equals("PAYMENT_PENDING") || status.equals("PAYMENT_COMPLETED");
    }

    public boolean isReturnable() {
        // 배송이 완료되어 사용자가 상품을 받았을 때만 교환 가능
        return status.equals("DELIVERED");
    }
}


