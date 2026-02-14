package com.nhnacademy.shop.admin.dto;

import com.nhnacademy.shop.common.enums.ProductType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@Data
public class DashboardHomeResponse {
    private KeyMetrics keyMetrics; // A. 핵심 지표
    private TodoAlerts todoAlerts; // B. 할 일 및 알림
    private ChartData chartData; // C. 주요 현황 그래프

    // --- A. 핵심 지표 (Key Metrics) ---
    @AllArgsConstructor
    @Data
    public static class KeyMetrics {
        private SalesMetric sales; // 실시간 매출
        private OrderMetric orders; // 실시간 주문
        private VisitorMetric visitors; // 방문자 현황
        private BigDecimal conversionRate; // 전환율 (%)


        @AllArgsConstructor
        @Data
        public static class SalesMetric {
            private BigDecimal todayAmount; // 금일 누적 결제 금액
            private Double growthRate; // 전일 대비 증감률 (%)

        }

        @AllArgsConstructor
        @Data
        public static class OrderMetric {
            private Long totalCount; // 금일 총 주문 건수
            private BigDecimal aov; // 객단가 (Average Order Value)
        }

        @Data
        public static class VisitorMetric {
            private Integer currentActive; // 현재 접속자 수
            private Integer todayTotal; // 금일 누적 방문자
            private Double growthRate; // 전일 대비 증감률 (%)
        }
    }

    // --- B. 할 일 및 알림 (To-Do & Alerts) ---
    @AllArgsConstructor
    @Data
    public static class TodoAlerts {
        private OrderFunnelStatus orderFunnel; // 주문 처리 현황
        private ClaimStatus urgentClaims; // 클레임 현황 (긴급)
        private List<LowStockProduct> lowStockProducts; // 재고 경고
        private CsStatus cs; // 고객 문의

        @AllArgsConstructor
        @Data
        public static class OrderFunnelStatus {
            private Integer paymentPending; // 입금대기
            private Integer paymentCompleted; // 결제완료
            private Integer shipped; // 발송완료
            private Integer delivering; // 배송중
            private Integer delivered; // 배송완료
            private Integer purchaseConfirm; // 구매확정
        }

        @AllArgsConstructor
        @Data
        public static class ClaimStatus {
            private Integer cancelRequest; // 취소요청
//            private Integer exchangeRequest; // 교환요청
            private Integer returnRequest; // 반품요청
        }

        @AllArgsConstructor
        @Data
        public static class LowStockProduct {
            private Long productId;
            private String productName;
            private Integer productStock;
        }

        @Data
        public static class CsStatus {
            private Integer unansweredInquiry; // 미답변 1:1 문의
            private Integer newReview; // 신규 상품평
        }
    }

    // --- C. 주요 현황 그래프 (Charts) ---
    @AllArgsConstructor
    @Data
    public static class ChartData {
        private List<HourlySalesTrend> hourlySales; // 시간대별 매출 추이
        private List<BestsellerItem> topBestsellers; // 베스트셀러 Top 5

        @Data
        public static class HourlySalesTrend {
            private Integer hour; // 시간 (0~23)
            private BigDecimal todayAmt; // 오늘 매출
            private BigDecimal yesterdayAmt; // 어제 매출
            private BigDecimal lastWeekAmt; // 지난주 동요일 매출
        }
        @AllArgsConstructor
        @Data
        public static class BestsellerItem {
            private Integer rank; // 순위
            private Long productId;
            private ProductTypeResponse productType;
            private String productName; // 상품명
            private String productImageUrl; // 표지 이미지 URL
            private Integer salesCount; // 금일 판매량

            public BestsellerItem(Integer rank, Long productId, ProductType productType, String productName, String productImageUrl, Integer salesCount) {
                this.rank = rank;
                this.productId = productId;
                this.productType = ProductTypeResponse.of(productType);
                this.productName = productName;
                this.productImageUrl = productImageUrl;
                this.salesCount = salesCount;
            }
        }
    }
}