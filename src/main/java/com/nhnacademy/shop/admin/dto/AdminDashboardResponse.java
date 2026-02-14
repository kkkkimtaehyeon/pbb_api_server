package com.nhnacademy.shop.admin.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class AdminDashboardResponse {
    private KpiDto kpi;
    private SalesSummaryDto salesSummary;
    private List<ProductStockDto> lowStockProducts;
    private OrderStatusSummaryDto orderStatus;

    @Data
    public static class KpiDto {
        private Long todaySales; // 오늘 매출
        private Long todayOrderCount; // 오늘 주문 수
        private Long pendingShipment; // 출고 대기 건수
        private Long refundRequests; // 환불 요청 건수 // 반품 요청??
        private Double salesGrowthRate; // 전일 대비 매출 증감률 (%)

        @QueryProjection
        public KpiDto(Long todaySales, Long todayOrderCount, Long pendingShipment, Long refundRequests, Double salesGrowthRate) {
            this.todaySales = todaySales;
            this.todayOrderCount = todayOrderCount;
            this.pendingShipment = pendingShipment;
            this.refundRequests = refundRequests;
            this.salesGrowthRate = salesGrowthRate;
        }
    }

    @Data
    public static class SalesSummaryDto {
        private List<DailySalesDto> dailySales; // 최근 7일 매출
        private Double averageOrderValue; // 평균 객단가
        private Double conversionRate; // 전환율

        @QueryProjection
        public SalesSummaryDto(List<DailySalesDto> dailySales, Double averageOrderValue, Double conversionRate) {
            this.dailySales = dailySales;
            this.averageOrderValue = averageOrderValue;
            this.conversionRate = conversionRate;
        }
    }

    @Data
    public static class DailySalesDto {
        private LocalDate date;
        private Long salesAmount;

        @QueryProjection
        public DailySalesDto(LocalDate date, Long salesAmount) {
            this.date = date;
            this.salesAmount = salesAmount;
        }
    }

    @Data
    public static class ProductStockDto {
        private Long productId;
        private String productName;
        private Integer stockQuantity;
        private Boolean outOfStock; // true = 품절

        @QueryProjection
        public ProductStockDto(Long productId, String productName, Integer stockQuantity, Boolean outOfStock) {
            this.productId = productId;
            this.productName = productName;
            this.stockQuantity = stockQuantity;
            this.outOfStock = outOfStock;
        }
    }

    @Data
    public static class OrderStatusSummaryDto {
        private Long paymentCompleted;
        private Long preparingShipment;
        private Long shipping;
        private Long delivered;
        private Long cancelRequested;

        @QueryProjection
        public OrderStatusSummaryDto(Long paymentCompleted, Long preparingShipment, Long shipping, Long delivered, Long cancelRequested) {
            this.paymentCompleted = paymentCompleted;
            this.preparingShipment = preparingShipment;
            this.shipping = shipping;
            this.delivered = delivered;
            this.cancelRequested = cancelRequested;
        }
    }
}