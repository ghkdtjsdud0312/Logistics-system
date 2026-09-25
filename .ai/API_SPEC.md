# API SPEC

> **상태: 확정 (2026-09-25)** — 방향 전환(ADR-011~018) 이후 재작성한 스펙이며 사용자 승인을 받았다. 변경은 승인 게이트다. 단, DECISION_LOG의 미결정 항목(출고 지시, 배차 취소 등)에 해당하는 API는 초안 기준으로 확정하되 해당 결정이 바뀌면 함께 수정한다.
> 이전 스펙(출고 계획, 차량 후보, 경로 최적화, 이상 탐지 API)은 폐기한다.

## 공통 규칙

- Base path: `/api` (기존 구현과 동일, `/api/v1` 미사용 — ADR-014)
- 요청 헤더: `X-Actor`(선택, URL 인코딩된 값, 없으면 `SYSTEM`) — 감사로그 사용자
- 성공 응답: `{ "success": true, "message": "...", "data": ... }` (기존 `ApiResponse<T>`)
- 오류 응답: `{ "code": "...", "message": "...", "errors": [...] }` (기존 `ErrorResponse`)
- 목록 조회는 `page`, `size`(기본 20) 페이지네이션을 사용한다. 시간은 ISO-8601.
- 상태값은 영문 enum으로 주고받고 한글 표기는 프런트 `constants/`에서 매핑한다.

## 기준정보

| Method | Path | 설명 | Request |
|---|---|---|---|
| POST | `/products` | 상품 등록 | `{code, name, unit, unitWeightKg}` |
| GET | `/products?keyword=` | 상품 목록 | - |
| POST | `/warehouses` | 창고 등록 | `{code, name}` |
| POST | `/warehouses/{id}/zones` | 구역 등록 | `{code, name}` |
| POST | `/zones/{id}/locations` | 위치 등록 | `{code}` |
| GET | `/warehouses/tree` | 창고→구역→위치 트리 | - |
| POST | `/vehicles` | 차량 등록 | `{vehicleNumber, vehicleType, capacityKg}` |
| GET | `/vehicles?status=` | 차량 목록 | - |
| PATCH | `/vehicles/{id}/status` | 차량 상태 변경 | `{status}` |
| POST | `/drivers` | 기사 등록 | `{driverCode, name, phone}` |
| GET | `/drivers?status=` | 기사 목록 | - |
| PATCH | `/drivers/{id}/status` | 기사 상태 변경 | `{status}` |

## 입고·재고

| Method | Path | 설명 | Request |
|---|---|---|---|
| POST | `/inbounds` | 입고 예정 등록 | `{partnerName, productId, quantity, inboundDate}` |
| GET | `/inbounds?status=` | 입고 목록 | - |
| GET | `/inbounds/{id}` | 입고 상세 | - |
| PATCH | `/inbounds/{id}/receive` | 입고완료 (→ 적치대기) | - |
| PATCH | `/inbounds/{id}/putaway` | 적치완료, 위치 재고 +입고수량 | `{locationId}` |
| GET | `/stocks?warehouseId=&zoneId=&keyword=&stockStatus=` | 재고 현황 | - |

`GET /stocks` 응답 행: `{warehouseName, locationCode, productCode, productName, onHand, reserved, available}`. `stockStatus`: `AVAILABLE`(가용>0) / `SOLD_OUT`(가용=0).

## 주문

| Method | Path | 설명 |
|---|---|---|
| POST | `/orders` | 주문 생성 + 재고 예약 |
| GET | `/orders?orderNo=&customerName=&status=&from=&to=` | 주문 목록 |
| GET | `/orders/{id}` | 주문 상세 |
| POST | `/orders/{id}/release` | 출고 지시 (피킹 작업 생성, → OUTBOUND_WAITING) |

```json
// POST /orders
{ "customerName": "김철수", "address": "서울시 강남구 ...", "phone": "010-0000-0000",
  "items": [{ "productId": 1, "quantity": 10 }] }
// 201 data
{ "id": 1, "orderNo": "ORD-001", "status": "RECEIVED" }
```

- 오류: `INSUFFICIENT_STOCK`(409, 가용재고 부족), `PRODUCT_NOT_FOUND`(404), `INVALID_ORDER_TRANSITION`(409).

```json
// GET /orders 행
{ "id": 1, "orderNo": "ORD-001", "customerName": "김철수", "productSummary": "생수 외 0건",
  "quantity": 10, "orderedAt": "2026-09-25T09:21:00Z", "status": "IN_DELIVERY", "deliveryStatus": "IN_DELIVERY" }

// GET /orders/{id}
{ "orderNo": "ORD-001", "status": "IN_DELIVERY", "customerName": "...", "address": "...", "phone": "...",
  "orderedAt": "...",
  "items": [{ "productCode": "WATER001", "productName": "생수 500ml",
              "orderedQty": 10, "pickedQty": 10, "loadedQty": 10, "deliveredQty": 0 }],
  "timeline": [{ "step": "RECEIVED", "done": true, "at": "..." }],
  "delivery": { "vehicleNumber": "12가1234", "driverName": "홍길동",
                "plannedStartAt": "...", "startedAt": "..." },
  "events": [{ "at": "...", "description": "피킹 완료" }] }
```

`timeline` 단계: RECEIVED, PICKED, PACKED, LOADED, DISPATCHED, IN_DELIVERY, DELIVERED. 단계별 시각(`at`)은 접수 단계만 채우고 나머지는 감사로그 연동(Day 3) 이후 채운다. `events`도 그때까지 빈 배열이다. `deliveryStatus`는 Shipment 상태이며 없으면 `null`.

## 피킹·포장

| Method | Path | 설명 | Request |
|---|---|---|---|
| GET | `/picking-tasks?status=` | 피킹 작업 목록 | - |
| PATCH | `/picking-tasks/{id}/start` | 피킹 시작 | - |
| PATCH | `/picking-tasks/{id}/complete` | 피킹 완료, 재고 차감 | `{pickedQty}` |
| GET | `/packing-tasks?status=` | 포장 작업 목록 | - |
| PATCH | `/packing-tasks/{id}/complete` | 포장 완료 | `{boxCode}` |

- 피킹 행: `{id, taskNo, orderNo, locationCode, productName, requestedQty, pickedQty, status}`
- 오류: `PICKED_QTY_MISMATCH`(422, 피킹수량 ≠ 요청수량), `PICKED_QTY_EXCEEDED`(422).

## 상차·배차

| Method | Path | 설명 | Request |
|---|---|---|---|
| GET | `/loadings/waiting-orders` | 상차 대기(포장완료) 주문 | - |
| POST | `/loadings` | 상차완료, 주문별 Shipment 생성 | `{orderIds: [1,2]}` |
| GET | `/shipments?status=&dispatchId=` | Shipment 목록(배차 대기는 `status=LOADED`) | - |
| POST | `/dispatches` | 배차 등록 | `{vehicleId, driverId, plannedStartAt, plannedArrivalAt, shipmentIds}` |
| GET | `/dispatches?status=` | 배차 목록 | - |
| GET | `/dispatches/{id}` | 배차 상세 | - |
| PATCH | `/dispatches/{id}/start` | 배송 시작 | - |
| PATCH | `/dispatches/{id}/cancel` | 배차 취소(REGISTERED만) | - |

- `POST /dispatches` 응답: `{id, dispatchNo, status, totalWeightKg, shipmentCount}`
- 오류: `VEHICLE_OVERLOAD`(422), `VEHICLE_UNAVAILABLE`(409), `DRIVER_UNAVAILABLE`(409), `SHIPMENT_ALREADY_DISPATCHED`(409), `ORDER_NOT_PACKED`(409).

## 배송현황·완료·실패

| Method | Path | 설명 | Request |
|---|---|---|---|
| GET | `/delivery-status` | 차량별 배송 현황 + 주문별 상태 | - |
| PATCH | `/shipments/{id}/deliver` | 배송 완료 | `{deliveredQty}` |
| PATCH | `/shipments/{id}/fail` | 배송 실패 (반품 자동 생성) | `{reason, detail}` |

```json
// GET /delivery-status
[{ "dispatchId": 1, "vehicleNumber": "12가1234", "driverName": "홍길동", "status": "IN_TRANSIT",
   "completed": 3, "total": 8, "startedAt": "...",
   "orders": [{ "shipmentId": 5, "orderNo": "ORD-001", "customerName": "김철수", "status": "DELIVERED" }] }]
```

- `reason`: `CUSTOMER_ABSENT | ADDRESS_ERROR | REFUSED | DAMAGED | OTHER`
- 오류: `DELIVERED_QTY_MISMATCH`(422), `SHIPMENT_NOT_IN_DELIVERY`(409).

## 반품

| Method | Path | 설명 | Request |
|---|---|---|---|
| GET | `/returns?status=` | 반품 목록 | - |
| GET | `/returns/{id}` | 반품 상세 | - |
| PATCH | `/returns/{id}/collect` | 회수 시작 (REQUESTED → COLLECTING) | - |
| PATCH | `/returns/{id}/collected` | 회수 완료 | - |
| PATCH | `/returns/{id}/receive` | 반품입고 (파손 아니면 재고 복구) | `{locationId}` (파손이면 생략 가능) |
| PATCH | `/returns/{id}/complete` | 처리 완료 | - |

- 목록 행: `{returnNo, orderNo, customerName, reason, quantity, status}`

## 감사로그

| Method | Path | 설명 |
|---|---|---|
| GET | `/audit-logs?from=&to=&actor=&target=&action=` | 감사로그 검색 |

행: `{occurredAt, actor, targetNo, action, fromStatus, toStatus}`

## 대시보드·SSE

| Method | Path | 설명 |
|---|---|---|
| GET | `/dashboard/summary` | 오늘의 현황 (Redis 캐시) |
| GET | `/dashboard/vehicles` | 차량 배송 현황 |
| GET | `/dashboard/events?limit=10` | 최근 물류 이벤트 |
| GET | `/events/logistics` | SSE 스트림 (`status-changed`) |

```json
// GET /dashboard/summary
{ "date": "2026-09-25",
  "orders": 128, "pickingWaiting": 24, "packingWaiting": 18, "loadingWaiting": 15,
  "inDelivery": 31, "delivered": 82, "failed": 3,
  "progress": { "RECEIVED": 128, "PICKING": 24, "PACKED": 18, "LOADED": 15, "IN_DELIVERY": 31 } }
```

- 집계 기준은 오늘 주문일(`ordered_at`)이다. `progress`는 단계별 **현재** 건수다(ADR-016).

## 공통 오류 코드

`VALIDATION_ERROR`(400), `NOT_FOUND`(404), `DUPLICATE_CODE`(409), `INVALID_*_TRANSITION`(409), `INTERNAL_ERROR`(500)
