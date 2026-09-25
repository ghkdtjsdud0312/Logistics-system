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
    CONCURRENT_UPDATE(HttpStatus.CONFLICT, "C006", "다른 요청이 먼저 처리되었습니다. 다시 시도하세요."),

    // Master
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "PD001", "상품 정보를 찾을 수 없습니다."),
    WAREHOUSE_NOT_FOUND(HttpStatus.NOT_FOUND, "WH001", "창고 정보를 찾을 수 없습니다."),
    ZONE_NOT_FOUND(HttpStatus.NOT_FOUND, "WH002", "구역 정보를 찾을 수 없습니다."),
    LOCATION_NOT_FOUND(HttpStatus.NOT_FOUND, "WH003", "위치 정보를 찾을 수 없습니다."),

    // Inbound
    INBOUND_NOT_FOUND(HttpStatus.NOT_FOUND, "IB001", "입고 정보를 찾을 수 없습니다."),
    INBOUND_INVALID_STATUS_TRANSITION(HttpStatus.CONFLICT, "IB002", "허용되지 않은 입고 상태 전이입니다."),

    // Order
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "OD001", "주문 정보를 찾을 수 없습니다."),
    INVALID_ORDER_TRANSITION(HttpStatus.CONFLICT, "OD002", "허용되지 않은 주문 상태 전이입니다."),

    // Warehouse work
    WORK_TASK_NOT_FOUND(HttpStatus.NOT_FOUND, "WK001", "작업 정보를 찾을 수 없습니다."),
    INVALID_TASK_TRANSITION(HttpStatus.CONFLICT, "WK002", "허용되지 않은 작업 상태 전이입니다."),
    PICKED_QTY_MISMATCH(HttpStatus.UNPROCESSABLE_ENTITY, "WK003", "피킹수량이 요청수량과 다릅니다."),
    PICKED_QTY_EXCEEDED(HttpStatus.UNPROCESSABLE_ENTITY, "WK004", "피킹수량이 요청수량을 초과했습니다."),

    // Loading / Dispatch
    ORDER_NOT_PACKED(HttpStatus.CONFLICT, "LD001", "포장이 완료된 주문만 상차할 수 있습니다."),
    SHIPMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "LD002", "배송 정보를 찾을 수 없습니다."),
    INVALID_SHIPMENT_TRANSITION(HttpStatus.CONFLICT, "LD003", "허용되지 않은 배송 상태 전이입니다."),
    SHIPMENT_ALREADY_DISPATCHED(HttpStatus.CONFLICT, "DP001", "이미 배차되었거나 상차되지 않은 배송입니다."),
    DISPATCH_NOT_FOUND(HttpStatus.NOT_FOUND, "DP002", "배차 정보를 찾을 수 없습니다."),
    INVALID_DISPATCH_TRANSITION(HttpStatus.CONFLICT, "DP003", "허용되지 않은 배차 상태 전이입니다."),
    VEHICLE_OVERLOAD(HttpStatus.UNPROCESSABLE_ENTITY, "DP004", "차량 적재량을 초과했습니다."),
    VEHICLE_UNAVAILABLE(HttpStatus.CONFLICT, "DP005", "선택한 차량은 배차할 수 없는 상태입니다."),
    DRIVER_UNAVAILABLE(HttpStatus.CONFLICT, "DP006", "선택한 기사는 배차할 수 없는 상태입니다."),

    // Delivery
    SHIPMENT_NOT_IN_DELIVERY(HttpStatus.CONFLICT, "DV001", "배송중인 건만 완료·실패 처리할 수 있습니다."),
    DELIVERED_QTY_MISMATCH(HttpStatus.UNPROCESSABLE_ENTITY, "DV002", "인도수량이 배송수량과 다릅니다."),

    // Return
    RETURN_NOT_FOUND(HttpStatus.NOT_FOUND, "RT001", "반품 정보를 찾을 수 없습니다."),
    INVALID_RETURN_TRANSITION(HttpStatus.CONFLICT, "RT002", "허용되지 않은 반품 상태 전이입니다."),
    RETURN_LOCATION_REQUIRED(HttpStatus.UNPROCESSABLE_ENTITY, "RT003", "재고를 복구할 위치를 지정해야 합니다."),

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
