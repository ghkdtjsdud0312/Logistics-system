package com.logistics.domain.inventory.application;

/** 재고 현황 검색용 상태. 가용재고 기준으로 나눈다. */
public enum StockStatus {
    AVAILABLE,
    SOLD_OUT;

    public boolean matches(int available) {
        return this == AVAILABLE ? available > 0 : available <= 0;
    }
}
