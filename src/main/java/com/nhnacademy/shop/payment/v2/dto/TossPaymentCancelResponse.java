package com.nhnacademy.shop.payment.v2.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
public class TossPaymentCancelResponse {
    private String transactionKey;
    private String cancelReason;
    private int taxExemptionAmount;
    private OffsetDateTime canceledAt;
    private int transferDiscountAmount;
    private int easyPayDiscountAmount;
    private String receiptKey;
    private int cancelAmount;
    private int taxFreeAmount;
    private int refundableAmount;
    private String cancelStatus;
    private String cancelRequestId;

    @Builder
    public TossPaymentCancelResponse(String transactionKey, String cancelReason, int taxExemptionAmount, OffsetDateTime canceledAt, int transferDiscountAmount, int easyPayDiscountAmount, String receiptKey, int cancelAmount, int taxFreeAmount, int refundableAmount, String cancelStatus, String cancelRequestId) {
        this.transactionKey = transactionKey;
        this.cancelReason = cancelReason;
        this.taxExemptionAmount = taxExemptionAmount;
        this.canceledAt = canceledAt;
        this.transferDiscountAmount = transferDiscountAmount;
        this.easyPayDiscountAmount = easyPayDiscountAmount;
        this.receiptKey = receiptKey;
        this.cancelAmount = cancelAmount;
        this.taxFreeAmount = taxFreeAmount;
        this.refundableAmount = refundableAmount;
        this.cancelStatus = cancelStatus;
        this.cancelRequestId = cancelRequestId;
    }

    public static TossPaymentCancelResponse mockResponse(BigDecimal amount, String reason) {
        return TossPaymentCancelResponse.builder()
                .cancelAmount(amount.toBigInteger().intValue())
                .cancelReason(reason)
                .canceledAt(OffsetDateTime.now())
                .build();

    }


}
