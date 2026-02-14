package com.nhnacademy.shop.admin.repository;

import com.nhnacademy.shop.admin.dto.DashboardHomeResponse;
import com.nhnacademy.shop.common.enums.OrderClaimStatus;
import com.nhnacademy.shop.common.enums.OrderClaimType;
import com.nhnacademy.shop.common.enums.OrderItemStatus;
import com.nhnacademy.shop.common.enums.PaymentType;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static com.nhnacademy.shop.order.v2.entity.QOrder.order;
import static com.nhnacademy.shop.order.v2.entity.QOrderClaim.orderClaim;
import static com.nhnacademy.shop.order.v2.entity.QOrderItem.orderItem;
import static com.nhnacademy.shop.payment.v2.entity.QPayment.payment;
import static com.nhnacademy.shop.product.entity.QProduct.product;

@RequiredArgsConstructor
@Repository
public class AdminOrderQueryRepository {
    private final JPAQueryFactory queryFactory;

    public DashboardHomeResponse getAdminDashboardData() {
        DashboardHomeResponse.KeyMetrics keyMetrics = getKeyMetrics();
        DashboardHomeResponse.TodoAlerts todoAlerts = getTodoAlerts();
        DashboardHomeResponse.ChartData chartData = getChartData();

        return new DashboardHomeResponse(keyMetrics, todoAlerts, chartData);
    }

    private DashboardHomeResponse.ChartData getChartData() {
        int bestSellerItemLimit = 10;
        List<DashboardHomeResponse.ChartData.BestsellerItem> bestsellerItems = findBestSellerItems(bestSellerItemLimit);
        List<DashboardHomeResponse.ChartData.HourlySalesTrend> hourlySales = findHourlySales();


        return new DashboardHomeResponse.ChartData(hourlySales, bestsellerItems);
    }

    private List<DashboardHomeResponse.ChartData.HourlySalesTrend> findHourlySales() {
        return null;
    }


    private List<DashboardHomeResponse.ChartData.BestsellerItem> findBestSellerItems(int limit) {
        NumberExpression<Integer> salesCount = orderItem.quantity.sum();
        NumberExpression<Integer> ranking =
                Expressions.numberTemplate(
                        Integer.class,
                        "dense_rank() over (order by {0} desc)",
                        salesCount
                );
        return queryFactory.select(
                        Projections.constructor(
                                DashboardHomeResponse.ChartData.BestsellerItem.class,
                                ranking,
                                product.id,
                                product.type,
                                product.name,
                                product.imageUrl,
                                salesCount
                        ))
                .from(product)
                .leftJoin(orderItem).on(orderItem.product.eq(product))
                .groupBy(product.id, product.name, product.imageUrl)
                .having(salesCount.gt(0))
                .orderBy(salesCount.desc())
                .limit(limit)
                .fetch();

    }

    private DashboardHomeResponse.TodoAlerts getTodoAlerts() {
        LocalDateTime start = LocalDateTime.now().minusDays(7);
        LocalDateTime end = LocalDateTime.now();
        int lowStockThreshold = 5;
        // orderItem 상태별 count 계산
        DashboardHomeResponse.TodoAlerts.OrderFunnelStatus orderFunnelStatus = calculateOrderItemStatusCount(start, end);
        // orderClaim 별 count 계산
        DashboardHomeResponse.TodoAlerts.ClaimStatus claimStatus = calculateOrderClaimStatusCount();
        List<DashboardHomeResponse.TodoAlerts.LowStockProduct> lowStockProducts = findLowStockProducts(lowStockThreshold);
        DashboardHomeResponse.TodoAlerts.CsStatus csStatus = null;
        return new DashboardHomeResponse.TodoAlerts(orderFunnelStatus, claimStatus, lowStockProducts, csStatus);

    }

    private List<DashboardHomeResponse.TodoAlerts.LowStockProduct> findLowStockProducts(int lowStockThreshold) {
        return queryFactory
                .select(
                        Projections.constructor(
                                DashboardHomeResponse.TodoAlerts.LowStockProduct.class,
                                product.id, product.name, product.stock
                        ))
                .from(product)
                .where(product.stock.loe(lowStockThreshold))
                .orderBy(product.stock.asc())
                .fetch();

    }

    private DashboardHomeResponse.TodoAlerts.ClaimStatus calculateOrderClaimStatusCount() {
        return queryFactory
                .select(
                        Projections.constructor(
                                DashboardHomeResponse.TodoAlerts.ClaimStatus.class,
                                Expressions.numberTemplate(Integer.class,
                                        "sum(if({0} = {1}, 1, 0))",
                                        orderClaim.type, OrderClaimType.CANCEL),
                                Expressions.numberTemplate(Integer.class,
                                        "sum(if({0} = {1} and {2} = {3}, 1, 0))",
                                        orderClaim.type, OrderClaimType.RETURNS, orderClaim.status, OrderClaimStatus.REQUESTED)
                        ))
                .from(orderClaim)
                .fetchOne();
    }

    private DashboardHomeResponse.TodoAlerts.OrderFunnelStatus calculateOrderItemStatusCount(LocalDateTime start, LocalDateTime end) {
        return queryFactory
                .select(Projections.constructor(
                        DashboardHomeResponse.TodoAlerts.OrderFunnelStatus.class,
                        Expressions.numberTemplate(Integer.class,
                                "sum(case when {0} = {1} then 1 else 0 end)",
                                orderItem.status, OrderItemStatus.PAYMENT_PENDING),

                        Expressions.numberTemplate(Integer.class,
                                "sum(case when {0} = {1} then 1 else 0 end)",
                                orderItem.status, OrderItemStatus.PAYMENT_COMPLETED),

                        Expressions.numberTemplate(Integer.class,
                                "sum(case when {0} = {1} then 1 else 0 end)",
                                orderItem.status, OrderItemStatus.SHIPPED),

                        Expressions.numberTemplate(Integer.class,
                                "sum(case when {0} = {1} then 1 else 0 end)",
                                orderItem.status, OrderItemStatus.DELIVERING),

                        Expressions.numberTemplate(Integer.class,
                                "sum(case when {0} = {1} then 1 else 0 end)",
                                orderItem.status, OrderItemStatus.DELIVERED),

                        Expressions.numberTemplate(Integer.class,
                                "sum(case when {0} = {1} then 1 else 0 end)",
                                orderItem.status, OrderItemStatus.PURCHASE_CONFIRMED)
                ))
                .from(orderItem)
                .join(orderItem.order, order)
                .where(order.orderedAt.between(start, end))
                .fetchOne();
    }

    private DashboardHomeResponse.KeyMetrics getKeyMetrics() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDate.now().atTime(LocalTime.MAX);
        LocalDateTime yesterdayStart = todayStart.minusDays(1);
        LocalDateTime yesterdayEnd = todayEnd.minusDays(1);

        // SalesMetric 계산
        BigDecimal todaySales = calculateTotalPaymentAmount(todayStart, todayEnd);
        BigDecimal yesterdaySales = calculateTotalPaymentAmount(yesterdayStart, yesterdayEnd);
        Double salesGrowthRate = calculateSalesGrowthRate(todaySales, yesterdaySales);
        DashboardHomeResponse.KeyMetrics.SalesMetric salesMetric = new DashboardHomeResponse.KeyMetrics.SalesMetric(todaySales, salesGrowthRate);

        // OrderMetric 계산
        DashboardHomeResponse.KeyMetrics.OrderMetric orderMetric = null;
        if (todaySales != null) {
            Long orderTotalCount = queryFactory
                    .select(order.count())
                    .from(order)
                    .where(order.orderedAt.between(todayStart, todayEnd))
                    .fetchOne();
            BigDecimal aov = todaySales.divide(BigDecimal.valueOf(orderTotalCount), RoundingMode.HALF_UP);
            orderMetric = new DashboardHomeResponse.KeyMetrics.OrderMetric(orderTotalCount, aov);
        }


        return new DashboardHomeResponse.KeyMetrics(salesMetric, orderMetric, null, null);
    }

    private BigDecimal calculateTotalPaymentAmount(LocalDateTime start, LocalDateTime end) {
        NumberExpression<BigDecimal> signedAmount =
                new CaseBuilder()
                        .when(payment.type.eq(PaymentType.CONFIRM))
                        .then(payment.amount)
                        .when(payment.type.eq(PaymentType.CANCEL))
                        .then(payment.amount.multiply(-1))
                        .otherwise(BigDecimal.ZERO);

        return queryFactory
                .select(signedAmount.sum())
                .from(payment)
                .where(payment.completedAt.between(start, end))
                .fetchOne();
    }

    private Double calculateSalesGrowthRate(BigDecimal todaySales, BigDecimal yesterdaySales) {
        if (todaySales == null || yesterdaySales == null) {
            return null;
        }
        double today = todaySales.doubleValue();
        double yesterday = yesterdaySales.doubleValue();
        return (today - yesterday) / yesterday * 100;
    }
}
