package com.nhnacademy.shop.stock.dto;

public record StockRollbackCommand(Long orderClaimId, Long productId, int rollbackQuantity) {

}
