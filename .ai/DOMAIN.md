# DOMAIN

## 도메인(패키지)과 Aggregate

| 도메인 | Aggregate | 책임 | 주요 불변식 |
|---|---|---|---|
| master | Product, Warehouse(구역·위치), Vehicle, Driver | 기준정보 | 코드 UNIQUE, 적재량·단위중량은 양수 |
| inbound | Inbound | 입고·적치 | 적치는 입고완료 이후 1개 위치로만 |
| inventory | Stock(상품+위치), | 현재/예약/가용 재고 | `reserved <= onHand`, 음수 금지 |
| order | Order(+OrderItem) | 주문과 주문 상태 | 상태 전이 순서 강제, 수량 양수 |
| warehouse-work | PickingTask, PackingTask | 피킹·포장 작업 | 피킹수량 == 요청수량일 때만 완료 |
| loading | Shipment | 상차와 주문별 배송 단위 | 포장완료 주문만 상차 |
| dispatch | Dispatch | 차량·기사와 Shipment 묶음 | 적재량 초과·중복 배정 금지 |
| delivery | (Shipment 상태 처리) | 배송 시작·완료·실패 | 인도수량 == 배송수량일 때만 완료 |
| returns | Return | 회수 흐름과 재고 복구 | 파손 사유는 재고 복구 금지 |
| audit | AuditLog | 상태 변경 이력 | append-only |
| dashboard | (조회 전용) | 집계·차량 현황·최근 이벤트 | 쓰기 없음 |

도메인 간 협력은 ID 참조와 Application Service 호출, Kafka 이벤트로만 한다(ADR-011).

## 상태 머신

### OrderStatus

`RECEIVED(주문접수) → OUTBOUND_WAITING(출고대기) → PICKING(피킹중) → PICKED(피킹완료) → PACKED(포장완료) → LOADED(상차완료) → DISPATCHED(배차완료) → IN_DELIVERY(배송중) → DELIVERED(배송완료) | FAILED(배송실패)`

- 주문 생성 = 주문접수(재고 예약 성공 시). 출고 지시 = 출고대기(피킹 작업 생성). 첫 피킹 작업 시작 = 피킹중. 모든 피킹 작업 완료 = 피킹완료(포장 작업 생성).
- `DELIVERED`, `FAILED`는 종결. 종결 상태에서 이동할 수 없다.

### InboundStatus

`EXPECTED(입고예정) → RECEIVED(입고완료) → PUTAWAY_WAITING(적치대기) → PUTAWAY_DONE(적치완료)`

- 입고완료(검수·수량 확정) 처리 직후 적치대기가 되고, 적치 위치를 지정하면 적치완료가 되며 재고가 증가한다.

### 작업 상태

- PickingTask: `WAITING → IN_PROGRESS → COMPLETED`
- PackingTask: `WAITING → COMPLETED`
- Shipment: `LOADED(상차완료) → DISPATCHED(배차완료) → IN_DELIVERY(배송중) → DELIVERED | FAILED`

### DispatchStatus

`REGISTERED(배차완료) → IN_TRANSIT(배송중) → COMPLETED`

- 소속 Shipment가 모두 종결(`DELIVERED`/`FAILED`)되면 자동으로 `COMPLETED`.
- 배송 시작 전(`REGISTERED`)에만 취소 가능(`CANCELLED`). 취소 시 Shipment는 `LOADED`로, 주문은 `LOADED`로 되돌린다.

### 차량·기사 상태

- Vehicle: `AVAILABLE(운행가능) / IN_OPERATION(운행중) / MAINTENANCE / INACTIVE`
- Driver: `AVAILABLE(운행가능) / DELIVERING(배송중) / OFF`
- 배차 등록 시 활성 배차 중복 여부를 검사하고, 배송 시작 시 `IN_OPERATION`/`DELIVERING`, 배차 완료 시 `AVAILABLE`로 복귀한다.

### ReturnStatus

`REQUESTED(회수요청) → COLLECTING(회수중) → COLLECTED(회수완료) → RETURN_RECEIVED(반품입고) → COMPLETED(처리완료)`

## 재고 규칙

```text
가용재고 = 현재재고(onHand) - 예약재고(reserved)
```

| 시점 | onHand | reserved |
|---|---|---|
| 적치완료 | + 입고수량 | - |
| 주문 생성(예약) | - | + 주문수량 (위치별 배분) |
| 피킹완료 | − 피킹수량 | − 피킹수량 |
| 반품입고(파손 아님) | + 반품수량 | - |

- 예약 배분은 가용재고가 있는 위치를 위치 코드 순으로 채운다. 총 가용재고가 부족하면 주문 전체를 거절한다.
- 재고 행은 낙관적 잠금(`version`)으로 동시 갱신을 보호한다.

## 수량 규칙

주문수량 ≥ 피킹수량 = 상차수량 = 배송수량 ≥ 인도수량. 이번 범위에서는 부분 처리를 허용하지 않으므로 완료 시점에 모두 같다(ADR-013). 주문 상세의 "출고수량" 컬럼은 `상차수량`으로 표기한다.

## 배차 규칙

```text
sum(shipment.items.quantity * product.unitWeightKg) <= vehicle.capacityKg
vehicle.status == AVAILABLE && driver.status == AVAILABLE
vehicle/driver가 REGISTERED 또는 IN_TRANSIT 배차에 속하지 않음
shipment.status == LOADED (다른 배차에 속하지 않음)
```

## 배송 실패와 반품

- 실패 사유: `CUSTOMER_ABSENT(고객 부재)`, `ADDRESS_ERROR(주소 오류)`, `REFUSED(수취 거부)`, `DAMAGED(상품 파손)`, `OTHER(기타)` + 상세 내용.
- 실패 처리 시 Shipment·주문은 `FAILED`가 되고 반품(`REQUESTED`)이 자동 생성된다. 재배송은 없다.
- 반품입고 시 사유가 `DAMAGED`가 아니면 지정 위치에 재고를 복구한다.

## 감사로그

모든 상태 변경은 이벤트(`fromStatus`, `toStatus`, `actor`, `orderId`)로 발행되고 Consumer가 `audit_log`에 기록한다. 주문 상세의 이벤트 이력은 같은 로그를 `orderId`로 조회한다.
