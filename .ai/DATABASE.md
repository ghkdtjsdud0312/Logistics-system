# DATABASE

## 원칙

- 도메인 간 물리적 Foreign Key와 JPA 연관은 두지 않고 ID 컬럼(논리 참조)만 둔다. 같은 도메인 내부(예: order → order_item)는 FK 허용.
- 식별자는 `Long` PK + 업무번호(`order_no` 등, UNIQUE). 기존 코드 관행을 따른다.
- 시간은 `timestamptz`(애플리케이션 UTC), 중량 `numeric(12,3)`, 상태는 문자열 enum.
- 재고·주문·배차 등 갱신 Aggregate는 `version`(낙관적 잠금).
- 스키마 관리 방식(현재 `ddl-auto: validate`)은 미결정 항목이다(`DECISION_LOG.md`).

## 테이블 개요

### master
| 테이블 | 주요 컬럼 | 제약 |
|---|---|---|
| product | id, code, name, unit, unit_weight_kg, active | code UNIQUE |
| warehouse | id, code, name | code UNIQUE |
| zone | id, warehouse_id, code, name | (warehouse_id, code) UNIQUE |
| location | id, zone_id, code | code UNIQUE (`A-01-01`) |
| vehicle | id, vehicle_number, vehicle_type, capacity_kg, status | number UNIQUE (기존 재사용) |
| driver | id, driver_code, name, phone, status | code UNIQUE (기존 재사용) |

### 입고·재고
| 테이블 | 주요 컬럼 | 제약 |
|---|---|---|
| inbound | id, inbound_no, partner_name, product_id, quantity, status, inbound_date, location_id(적치 위치, null 가능) | quantity > 0 |
| stock | id, product_id, location_id, on_hand, reserved, version | (product_id, location_id) UNIQUE, on_hand >= reserved >= 0 |

### 주문·작업
| 테이블 | 주요 컬럼 | 제약 |
|---|---|---|
| orders | id, order_no, customer_name, address, phone, status, ordered_at, version | order_no UNIQUE |
| order_item | id, order_id(FK), product_id, quantity, picked_qty, loaded_qty, delivered_qty | quantity > 0 |
| stock_reservation | id, order_item_id, location_id, quantity | quantity > 0 |
| picking_task | id, task_no, order_id, location_id, product_id, requested_qty, picked_qty, status | picked_qty <= requested_qty |
| packing_task | id, task_no, order_id, box_code, status | order_id UNIQUE |

### 상차·배차·배송
| 테이블 | 주요 컬럼 | 제약 |
|---|---|---|
| shipment | id, order_id, dispatch_id(null), status, delivered_at, fail_reason, fail_detail | order_id UNIQUE |
| dispatch | id, dispatch_no, vehicle_id, driver_id, status, planned_start_at, planned_arrival_at, started_at, version | dispatch_no UNIQUE |

### 반품·감사·이벤트
| 테이블 | 주요 컬럼 | 제약 |
|---|---|---|
| return_order | id, return_no, order_id, shipment_id, reason, quantity, status, location_id(null) | |
| audit_log | id, occurred_at, actor, target_type, target_id, target_no, order_id(null), action, from_status, to_status | append-only |
| processed_event | consumer_name, event_id, processed_at | 복합 PK |

## 구현 시 달라진 점

- 실제 테이블: `product, warehouse, zone, location, vehicle, driver, inbound, stock, stock_reservation, orders, order_item, picking_task, packing_task, shipment, dispatch, return_order, audit_log, processed_event`.
- 배차와 Shipment의 연결은 별도 매핑 테이블 없이 `shipment.dispatch_id`로 표현한다.
- `audit_log`에는 `event_id`를 두지 않고 `processed_event(consumer_name, event_id)`로 멱등 처리한다.
- 스키마는 `JPA_DDL_AUTO=update`로 생성한다(ADR-019).

## 인덱스

- `orders(status, ordered_at)`, `orders(customer_name)`
- `stock(product_id)`, `picking_task(order_id)`, `picking_task(status)`
- `shipment(dispatch_id)`, `shipment(status)`
- `dispatch(status)`
- `audit_log(order_id, occurred_at)`, `audit_log(occurred_at)`

## 기존 테이블 처리

| 기존 | 처리 |
|---|---|
| vehicle, driver | 재사용(컬럼 조정 필요 시 승인 게이트) |
| inbound | 재정의(컬럼·상태 변경 필요) |
| outbound, outbound_item | 폐기 예정 |
| dispatch, route_stop, dispatch_status_history/projection | 재정의 또는 폐기 |
| anomaly, event_cursor | 폐기 예정 |
| delivery | 재정의(Shipment로 대체) 또는 폐기 |

코드 정리는 별도 승인 후 진행한다. 파괴적 변경은 승인 게이트다.
