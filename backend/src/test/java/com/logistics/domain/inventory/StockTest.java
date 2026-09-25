package com.logistics.domain.inventory;

import com.logistics.domain.inventory.domain.Stock;
import com.logistics.global.error.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StockTest {

    private Stock stockOf(int onHand) {
        Stock stock = new Stock(1L, 1L);
        stock.increase(onHand);
        return stock;
    }

    @Test
    @DisplayName("가용재고는 현재재고에서 예약재고를 뺀 값이다")
    void available() {
        Stock stock = stockOf(100);
        stock.reserve(10);

        assertThat(stock.getOnHand()).isEqualTo(100);
        assertThat(stock.getReserved()).isEqualTo(10);
        assertThat(stock.getAvailable()).isEqualTo(90);
    }

    @Test
    @DisplayName("가용재고를 초과해 예약할 수 없고 가용과 같으면 예약된다")
    void reserve_boundary() {
        Stock stock = stockOf(10);

        assertThatThrownBy(() -> stock.reserve(11)).isInstanceOf(BusinessException.class);
        stock.reserve(10);
        assertThat(stock.getAvailable()).isZero();
    }

    @Test
    @DisplayName("피킹 차감은 현재재고와 예약재고를 함께 줄이고 예약보다 많이는 차감할 수 없다")
    void consumeReserved() {
        Stock stock = stockOf(100);
        stock.reserve(10);

        assertThatThrownBy(() -> stock.consumeReserved(11)).isInstanceOf(BusinessException.class);
        stock.consumeReserved(10);
        assertThat(stock.getOnHand()).isEqualTo(90);
        assertThat(stock.getReserved()).isZero();
        assertThat(stock.getAvailable()).isEqualTo(90);
    }

    @Test
    @DisplayName("0 이하 수량은 거부한다")
    void nonPositive() {
        Stock stock = stockOf(5);

        assertThatThrownBy(() -> stock.increase(0)).isInstanceOf(BusinessException.class);
        assertThatThrownBy(() -> stock.reserve(-1)).isInstanceOf(BusinessException.class);
    }
}
