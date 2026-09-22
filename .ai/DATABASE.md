# DATABASE

## 원칙

- 각 엔티티는 데이터 성격에 따라 테이블로 매핑되며, Modular Monolith 원칙에 따라 도메인 간 물리적 Foreign Key Join은 최소화하고 논리적 ID 참조를 지향한다.
- 캐시 저장소(Redis)의 Cache-Aside 정책은 `ARCHITECTURE.md`의 캐시 정책 절을 따른다.

## 테이블 개요

| 테이블 | 주요 컬럼 | 핵심 제약 |
|---|---|---|
| partner | id, name, code | code UNIQUE |
| inbound | id, partner_id, status, expected_at, completed_at | status CHECK |
| inbound_item | id, inbound_id, sku, expected_qty, inspected_qty, unit_weight_kg, unit_volume_m3 | 수량/단위값 >= 0 |
| outbound_plan | id, status, destination_name, x, y, priority, requested_delivery_at | 좌표 NOT NULL |
| outbound_item | id, outbound_plan_id, inbound_item_id, quantity | quantity > 0 |
| vehicle | id, plate_number, type, max_weight_kg, max_volume_m3, status | plate_number UNIQUE |
| driver | id, name, status | MVP 개인정보 최소화 |
| dispatch | id, vehicle_id, driver_id, status, planned_start_at, total_weight_kg, total_volume_m3 | 낙관적 잠금 version |
| dispatch_outbound | dispatch_id, outbound_plan_id | 활성 중복은 서비스+잠금으로 차단 |
| route_stop | id, dispatch_id, outbound_plan_id, sequence_no, status, distance_from_previous | (dispatch_id, sequence_no) UNIQUE |
| dispatch_history | id, dispatch_id, from_status, to_status, actor, occurred_at, description | append-only |
| anomaly | id, dispatch_id, type, severity, status, detected_at, fingerprint | fingerprint UNIQUE |
| processed_event | consumer_name, event_id, processed_at | 복합 PK, 멱등 Consumer |

### 실제 구현 (입고/출고, 단순화 버전)

| 테이블 | 주요 컬럼 | 핵심 제약 |
|---|---|---|
| inbound | id, item_name, quantity, warehouse_location, status, inspected_quantity | inspected_quantity는 COMPLETED 전엔 null |
| outbound | id, destination, status | items로 물량 구성, 자체 quantity 컬럼 없음 |
| outbound_item | id, outbound_id(FK), inbound_id(FK 아님, ID 참조), quantity | outbound_id는 outbound 소속(같은 도메인), inbound_id는 타 도메인이라 FK 미설정 |

## 데이터 타입 원칙

- 식별자: UUID
- 시간: `timestamptz`, 애플리케이션 UTC
- 중량/부피/거리: `numeric(12,3)`
- 상태: 문자열 enum + 애플리케이션 검증
- 낙관적 잠금: 주요 Aggregate에 `version` 컬럼

## 인덱스

- `inbound(status, expected_at)`
- `outbound_plan(status, requested_delivery_at)`
- `vehicle(status)`
- `dispatch(status, planned_start_at)`
- `dispatch_history(dispatch_id, occurred_at)`
- `anomaly(status, detected_at)`

## 마이그레이션 규칙

- Flyway만 사용하고 이미 적용된 파일을 수정하지 않는다.
- 파괴적 변경은 승인 게이트다.
- 테스트 스키마도 운영과 동일한 PostgreSQL을 사용한다.

