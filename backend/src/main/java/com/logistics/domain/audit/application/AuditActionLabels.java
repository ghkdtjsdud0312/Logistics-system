package com.logistics.domain.audit.application;

import java.util.Map;

/** 감사로그의 (대상 유형, 작업 코드)를 사람이 읽는 한글로 바꾼다. 없는 조합은 작업 코드를 그대로 쓴다. */
public final class AuditActionLabels {

    private static final Map<String, String> LABELS = Map.ofEntries(
            Map.entry("ORDER:CREATE", "주문 생성"),
            Map.entry("ORDER:RELEASE", "출고 지시"),
            Map.entry("ORDER:PICK_START", "피킹 시작"),
            Map.entry("ORDER:PICK_COMPLETE", "피킹 완료"),
            Map.entry("ORDER:PACK_COMPLETE", "포장 완료"),
            Map.entry("ORDER:LOAD_COMPLETE", "상차 완료"),
            Map.entry("ORDER:DISPATCH", "배차 완료"),
            Map.entry("ORDER:DISPATCH_CANCEL", "배차 취소"),
            Map.entry("ORDER:DELIVERY_START", "배송 시작"),
            Map.entry("ORDER:DELIVER", "배송 완료"),
            Map.entry("ORDER:DELIVERY_FAIL", "배송 실패"),
            Map.entry("INBOUND:CREATE", "입고 예정 등록"),
            Map.entry("INBOUND:RECEIVE", "입고 완료"),
            Map.entry("INBOUND:READY_FOR_PUTAWAY", "적치 대기"),
            Map.entry("INBOUND:PUTAWAY", "적치 완료"),
            Map.entry("DISPATCH:REGISTER", "배차 등록"),
            Map.entry("DISPATCH:START", "배차 배송 시작"),
            Map.entry("DISPATCH:CANCEL", "배차 취소"),
            Map.entry("DISPATCH:COMPLETE", "배차 종료"),
            Map.entry("PICKING_TASK:PICK_COMPLETE", "피킹 작업 완료"),
            Map.entry("PACKING_TASK:PACK_COMPLETE", "포장 작업 완료"),
            Map.entry("RETURN:CREATE", "반품 요청"),
            Map.entry("RETURN:COLLECT_START", "회수 시작"),
            Map.entry("RETURN:COLLECTED", "회수 완료"),
            Map.entry("RETURN:RETURN_RECEIVE", "반품 입고"),
            Map.entry("RETURN:COMPLETE", "반품 처리 완료"));

    private AuditActionLabels() {
    }

    public static String of(String targetType, String action) {
        return LABELS.getOrDefault(targetType + ":" + action, action);
    }
}
