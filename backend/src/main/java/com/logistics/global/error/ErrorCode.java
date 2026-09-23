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

    // Inbound
    INBOUND_NOT_FOUND(HttpStatus.NOT_FOUND, "IB001", "입고 정보를 찾을 수 없습니다."),
    INBOUND_INVALID_STATUS_TRANSITION(HttpStatus.CONFLICT, "IB002", "허용되지 않은 입고 상태 전이입니다."),

    // Outbound
    OUTBOUND_NOT_FOUND(HttpStatus.NOT_FOUND, "OB001", "출고 정보를 찾을 수 없습니다."),
    OUTBOUND_INVALID_STATUS_TRANSITION(HttpStatus.CONFLICT, "OB002", "허용되지 않은 출고 상태 전이입니다."),
    OUTBOUND_QUANTITY_EXCEEDED(HttpStatus.UNPROCESSABLE_ENTITY, "OB003", "가용 입고 수량을 초과했습니다."),
    OUTBOUND_SOURCE_INBOUND_NOT_COMPLETED(HttpStatus.UNPROCESSABLE_ENTITY, "OB004", "검수 완료되지 않은 입고는 출고 대상으로 선택할 수 없습니다."),

    // Dispatch
    DISPATCH_NOT_FOUND(HttpStatus.NOT_FOUND, "DP001", "배차 정보를 찾을 수 없습니다."),
    DISPATCH_OPTIMIZATION_FAILED(HttpStatus.UNPROCESSABLE_ENTITY, "DP002", "배차 최적화에 실패했습니다."),
    DISPATCH_OVER_CAPACITY(HttpStatus.UNPROCESSABLE_ENTITY, "DP003", "차량 적재 한도를 초과했습니다."),
    DISPATCH_VEHICLE_UNAVAILABLE(HttpStatus.CONFLICT, "DP004", "선택한 차량은 배차할 수 없는 상태입니다."),
    DISPATCH_DRIVER_SCHEDULE_CONFLICT(HttpStatus.CONFLICT, "DP005", "선택한 기사는 해당 시간에 이미 배차가 있습니다."),
    DISPATCH_DUPLICATE_ASSIGNMENT(HttpStatus.CONFLICT, "DP006", "이미 다른 배차에 포함된 출고 계획입니다."),
    DISPATCH_INVALID_STATUS_TRANSITION(HttpStatus.CONFLICT, "DP007", "허용되지 않은 배차 상태 전이입니다."),
    DISPATCH_STALE_VERSION(HttpStatus.CONFLICT, "DP008", "다른 요청이 먼저 배차 상태를 변경했습니다. 다시 조회 후 시도하세요."),
    DISPATCH_STOPS_NOT_DELIVERED(HttpStatus.CONFLICT, "DP009", "모든 경유지가 배송 완료되지 않아 배차를 완료할 수 없습니다."),
    ROUTE_STOP_NOT_FOUND(HttpStatus.NOT_FOUND, "DP010", "경유지 정보를 찾을 수 없습니다."),
    ROUTE_STOP_INVALID_TRANSITION(HttpStatus.CONFLICT, "DP011", "허용되지 않은 경유지 상태 전이입니다."),

    // Vehicle
    VEHICLE_NOT_FOUND(HttpStatus.NOT_FOUND, "VH001", "차량 정보를 찾을 수 없습니다."),

    // Driver
    DRIVER_NOT_FOUND(HttpStatus.NOT_FOUND, "DR001", "기사 정보를 찾을 수 없습니다."),

    // Delivery
    DELIVERY_NOT_FOUND(HttpStatus.NOT_FOUND, "DV001", "배송 정보를 찾을 수 없습니다."),

    // Anomaly
    ANOMALY_NOT_FOUND(HttpStatus.NOT_FOUND, "AN001", "이상 정보를 찾을 수 없습니다."),
    ANOMALY_INVALID_STATUS_TRANSITION(HttpStatus.CONFLICT, "AN002", "허용되지 않은 이상 상태 전이입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
