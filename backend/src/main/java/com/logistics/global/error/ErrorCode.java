package com.logistics.global.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 공통 에러 코드 정의
 * - 도메인별 예외는 이 Enum에 값을 추가하여 사용
 */
@Getter
public enum ErrorCode {

    // Common
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "입력 값이 올바르지 않습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C002", "허용되지 않은 HTTP 메서드입니다."),
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "C003", "요청한 리소스를 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C004", "서버 내부 오류가 발생했습니다."),
    DUPLICATE_CODE(HttpStatus.CONFLICT, "C005", "이미 사용 중인 코드입니다."),

    // Master
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "PD001", "상품 정보를 찾을 수 없습니다."),
    WAREHOUSE_NOT_FOUND(HttpStatus.NOT_FOUND, "WH001", "창고 정보를 찾을 수 없습니다."),
    ZONE_NOT_FOUND(HttpStatus.NOT_FOUND, "WH002", "구역 정보를 찾을 수 없습니다."),
    LOCATION_NOT_FOUND(HttpStatus.NOT_FOUND, "WH003", "위치 정보를 찾을 수 없습니다."),

    // Inbound
    INBOUND_NOT_FOUND(HttpStatus.NOT_FOUND, "IB001", "입고 정보를 찾을 수 없습니다."),
    INBOUND_INVALID_STATUS_TRANSITION(HttpStatus.CONFLICT, "IB002", "허용되지 않은 입고 상태 전이입니다."),

    // Inventory
    INSUFFICIENT_STOCK(HttpStatus.CONFLICT, "IV001", "가용 재고가 부족합니다."),

    // Vehicle
    VEHICLE_NOT_FOUND(HttpStatus.NOT_FOUND, "VH001", "차량 정보를 찾을 수 없습니다."),

    // Driver
    DRIVER_NOT_FOUND(HttpStatus.NOT_FOUND, "DR001", "기사 정보를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
