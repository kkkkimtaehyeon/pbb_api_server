package com.nhnacademy.shop.order.claim.dto;//package com.nhnacademy.shop.order.claim.dto;
//
//import com.nhnacademy.shop.common.enums.OrderClaimStatus;
//import com.nhnacademy.shop.common.enums.OrderClaimType;
//import com.nhnacademy.shop.common.enums.OrderItemStatus;
//import com.nhnacademy.shop.member.v2.entity.Member;
//import com.nhnacademy.shop.order.v2.entity.OrderClaim;
//import com.nhnacademy.shop.order.v2.entity.OrderItem;
//import com.nhnacademy.shop.order.v2.entity.Orders;
//import com.nhnacademy.shop.payment.v2.entity.Payment;
//import com.nhnacademy.shop.product.entity.Product;
//import com.querydsl.core.annotations.QueryProjection;
//import lombok.Data;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//
//@Data
//public class OrderClaimListResponse {
//    // claim
//    Long claimId;
//    OrderClaimType claimType;
//    OrderClaimStatus claimStatus;
//    String claimReason;
//    String claimReturnTrackingNumber;
//    LocalDateTime claimedAt;
//    LocalDateTime claimRefundedAt;
//    LocalDateTime claimCompletedAt;
//
//    // order
//    String orderId;
//    LocalDateTime orderedAt;
//
//    // orderItem
//    Long orderItemId;
//    OrderItemStatus orderItemStatus;
//    BigDecimal orderItemPrice;
//    BigDecimal orderItemDiscountAmount;
//    BigDecimal orderItemPaymentAmount;
//    Integer orderItemQuantity;
//
//    // product
//    Long productId;
//    String productImageUrl;
//    String productName;
//
//    // payment
//    String paymentMethod;
//    BigDecimal paymentAmount;
//
//
//    // member
//    Long memberId;
//    String memberName;
//
//    @QueryProjection
//    public OrderClaimListResponse(Long claimId, OrderClaimType claimType, OrderClaimStatus claimStatus, String claimReason, LocalDateTime claimedAt, String orderId, LocalDateTime orderedAt, Long orderItemId, OrderItemStatus orderItemStatus, BigDecimal orderItemPrice, BigDecimal orderItemDiscountAmount, BigDecimal orderItemPaymentAmount, Integer orderItemQuantity, Long productId, String productImageUrl, String productName, String paymentMethod, BigDecimal paymentAmount, Long memberId, String memberName) {
//        this.claimId = claimId;
//        this.claimType = claimType;
//        this.claimStatus = claimStatus;
//        this.claimReason = claimReason;
//        this.claimedAt = claimedAt;
//        this.orderId = orderId;
//        this.orderedAt = orderedAt;
//        this.orderItemId = orderItemId;
//        this.orderItemStatus = orderItemStatus;
//        this.orderItemPrice = orderItemPrice;
//        this.orderItemDiscountAmount = orderItemDiscountAmount;
//        this.orderItemPaymentAmount = orderItemPaymentAmount;
//        this.orderItemQuantity = orderItemQuantity;
//        this.productId = productId;
//        this.productImageUrl = productImageUrl;
//        this.productName = productName;
//        this.paymentMethod = paymentMethod;
//        this.paymentAmount = paymentAmount;
//        this.memberId = memberId;
//        this.memberName = memberName;
//    }
//
//    @QueryProjection
//    public OrderClaimListResponse(OrderClaim orderClaim, Orders order, OrderItem orderItem, Product product, Payment payment, Member member) {
//        this(
//                orderClaim.getId(),
//                orderClaim.getType(),
//                orderClaim.getStatus(),
//                orderClaim.getReason(),
//                orderClaim.getCreatedAt(),
//                order.getId(),
//                order.getOrderedAt(),
//                orderItem.getId(),
//                orderItem.getStatus(),
//                orderItem.getPrice(),
//                orderItem.getDiscountAmount(),
//                orderItem.getPaymentPrice(),
//                orderItem.getQuantity(),
//                product.getId(),
//                product.getImageUrl(),
//                product.getName(),
//                "toss",
//                payment.getAmount(),
//                member.getId(),
//                member.getName()
//
//        );
//    }
//}


import com.nhnacademy.shop.common.enums.OrderClaimStatus;
import com.nhnacademy.shop.common.enums.OrderClaimType;
import com.nhnacademy.shop.common.enums.OrderItemStatus;
import com.nhnacademy.shop.member.v2.entity.Member;
import com.nhnacademy.shop.order.v2.entity.Order;
import com.nhnacademy.shop.order.v2.entity.OrderClaim;
import com.nhnacademy.shop.order.v2.entity.OrderItem;
import com.nhnacademy.shop.payment.v2.entity.Payment;
import com.nhnacademy.shop.product.entity.Product;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderClaimListResponse {

    private ClaimInfo claim;
    private OrderInfo order;
    private OrderItemInfo orderItem;
    private ProductInfo product;
    private PaymentInfo payment;
    private MemberInfo member;

    public OrderClaimListResponse(ClaimInfo claim, OrderInfo order, OrderItemInfo orderItem, ProductInfo product, PaymentInfo payment, MemberInfo member) {
        this.claim = claim;
        this.order = order;
        this.orderItem = orderItem;
        this.product = product;
        this.payment = payment;
        this.member = member;
    }


    @QueryProjection
    public OrderClaimListResponse(
            OrderClaim orderClaim,
            Order order,
            OrderItem orderItem,
            Product product,
            Payment payment,
            Member member
    ) {
        this(
                new ClaimInfo(
                        orderClaim.getId(),
                        orderClaim.getType(),
                        orderClaim.getStatus(),
                        orderClaim.getReason(),
                        orderClaim.getReturnTrackingNumber(),
                        orderClaim.getCreatedAt(),
                        orderClaim.getRefundedAt(),
                        orderClaim.getStockRolledBackAt()
                ),
                new OrderInfo(
                        order.getId(),
                        order.getOrderedAt()
                ),
                new OrderItemInfo(
                        orderItem.getId(),
                        orderItem.getStatus(),
                        orderItem.getPrice(),
                        orderItem.getDiscountAmount(),
                        orderItem.getPrice(),
                        orderItem.getQuantity()
                ),
                new ProductInfo(
                        product.getId(),
                        product.getName(),
                        product.getImageUrl()
                ),
                new PaymentInfo(
//                        payment.getMethod(),
                        "toss",
                        payment.getAmount()
                ),
                new MemberInfo(
                        member.getId(),
                        member.getName()
                )
        );
    }


    /* ===== inner DTOs ===== */

    @Data
    public static class ClaimInfo {
        private Long id;
        private OrderClaimType type;
        private OrderClaimStatus status;
        private String reason;
        private String returnTrackingNumber;
        private LocalDateTime claimedAt;
        private LocalDateTime refundedAt;
        private LocalDateTime stockRolledBackAt;

        public ClaimInfo(Long id, OrderClaimType type, OrderClaimStatus status, String reason, String returnTrackingNumber, LocalDateTime claimedAt, LocalDateTime refundedAt, LocalDateTime stockRolledBackAt) {
            this.id = id;
            this.type = type;
            this.status = status;
            this.reason = reason;
            this.returnTrackingNumber = returnTrackingNumber;
            this.claimedAt = claimedAt;
            this.refundedAt = refundedAt;
            this.stockRolledBackAt = stockRolledBackAt;
        }
    }

    @Data
    public static class OrderInfo {
        private String id;
        private LocalDateTime orderedAt;

        public OrderInfo(String id, LocalDateTime orderedAt) {
            this.id = id;
            this.orderedAt = orderedAt;
        }
    }

    @Data
    public static class OrderItemInfo {
        private Long id;
        private OrderItemStatus status;
        private BigDecimal price;
        private BigDecimal discountAmount;
        private BigDecimal paymentAmount;
        private Integer quantity;

        public OrderItemInfo(Long id, OrderItemStatus status, BigDecimal price, BigDecimal discountAmount, BigDecimal paymentAmount, Integer quantity) {
            this.id = id;
            this.status = status;
            this.price = price;
            this.discountAmount = discountAmount;
            this.paymentAmount = paymentAmount;
            this.quantity = quantity;
        }
    }

    @Data
    public static class ProductInfo {
        private Long id;
        private String name;
        private String imageUrl;

        public ProductInfo(Long id, String name, String imageUrl) {
            this.id = id;
            this.name = name;
            this.imageUrl = imageUrl;
        }
    }

    @Data
    public static class PaymentInfo {
        private String method;
        private BigDecimal amount;

        public PaymentInfo(String method, BigDecimal amount) {
            this.method = method;
            this.amount = amount;
        }
    }

    @Data
    public static class MemberInfo {
        private Long id;
        private String name;

        public MemberInfo(Long id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}