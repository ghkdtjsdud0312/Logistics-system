package com.logistics.global.error;

import lombok.Getter;

/**
 * 비즈니스 로직에서 발생하는 공통 예외
 * - 도메인 서비스에서 throw new BusinessException(ErrorCode.XXX) 형태로 사용
 */
@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
