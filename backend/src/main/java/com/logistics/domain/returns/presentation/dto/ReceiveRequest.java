package com.logistics.domain.returns.presentation.dto;

/** 반품입고 요청. 상품 파손이면 locationId를 생략할 수 있다. */
public record ReceiveRequest(Long locationId) {
}
