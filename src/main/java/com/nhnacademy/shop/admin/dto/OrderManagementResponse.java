package com.nhnacademy.shop.admin.dto;

import com.nhnacademy.shop.common.enums.OrderStatus;
import com.nhnacademy.shop.order.v2.dto.OrderSimpleResponse;
import lombok.Data;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.Arrays;
import java.util.List;

@Data
public class OrderManagementResponse {
    List<OrderStatusResponse> orderStatuses;
    Page<OrderSimpleResponse> orders;

    public OrderManagementResponse(OrderStatus[] orderStatuses, Page<OrderSimpleResponse> orders) {
        this.orderStatuses = Arrays.stream(orderStatuses).map(OrderStatusResponse::new).toList();
        this.orders = orders;
    }
}

@Getter
class OrderStatusResponse {
    String status;
    String displayName;

    public OrderStatusResponse(OrderStatus status) {
        this.status = status.toString();
        this.displayName = status.getDisplayName();
    }
}
