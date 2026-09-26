package com.logistics.global.reference;

/** 각 도메인이 자기 데이터에서 기준정보 ID를 참조하는지 알려주는 포트 (Repository 직접 주입 대신 사용) */
public interface ReferenceChecker {

    boolean isReferenced(ReferenceType type, Long id);
}
