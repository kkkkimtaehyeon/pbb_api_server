package com.nhnacademy.shop.stock;


import com.nhnacademy.shop.stock.dto.StockRollbackCommand;

public interface StockService {
    void rollbackStock(StockRollbackCommand command);
}
