package com.nhnacademy.shop.order.claim.repository;

import com.nhnacademy.shop.common.enums.OrderClaimType;
import com.nhnacademy.shop.order.claim.dto.OrderClaimListResponse;
import com.nhnacademy.shop.order.claim.dto.QOrderClaimListResponse;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.nhnacademy.shop.member.v2.entity.QMember.member;
import static com.nhnacademy.shop.order.v2.entity.QOrderClaim.orderClaim;
import static com.nhnacademy.shop.order.v2.entity.QOrderItem.orderItem;
import static com.nhnacademy.shop.order.v2.entity.QOrders.orders;
import static com.nhnacademy.shop.payment.v2.entity.QPayment.payment;
import static com.nhnacademy.shop.product.entity.QProduct.product;

@RequiredArgsConstructor
@Repository
public class OrderClaimQueryRepository {
    private final JPAQueryFactory queryFactory;

    public Page<OrderClaimListResponse> findAll(OrderClaimType claimType, Pageable pageable) {
        List<OrderClaimListResponse> orderClaims = queryFactory
                .select(new QOrderClaimListResponse(
                        orderClaim,
                        orders,
                        orderItem,
                        product,
                        payment,
                        member
                ))
                .from(orderClaim)
                .innerJoin(orderClaim.orderItem, orderItem)
                .innerJoin(orderItem.product, product)
                .innerJoin(orderItem.order, orders)
                .innerJoin(payment).on(payment.order.eq(orders))
                .innerJoin(orders.member, member)
                .where(eqClaimType(claimType))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(orderClaim.createdAt.desc())
                .fetch();

        Long count = queryFactory
                .select(orderClaim.count())
                .from(orderClaim)
                .innerJoin(orderClaim.orderItem, orderItem)
                .innerJoin(orderItem.product, product)
                .innerJoin(orderItem.order, orders)
                .innerJoin(payment).on(payment.order.eq(orders))
                .innerJoin(orders.member, member)
                .where(eqClaimType(claimType))
                .fetchFirst();
        return new PageImpl<>(orderClaims, pageable, count);
    }

    private BooleanExpression eqClaimType(OrderClaimType type) {
        return type == null ? null : orderClaim.type.eq(type);
    }
}
