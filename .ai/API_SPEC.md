# API SPEC

## 설계 프로세스 규칙

모든 API는 RESTful 가이드라인을 따르며 JSON 형식으로 통신한다. 코드를 작성하기 전에 반드시 필요한 API의 URI, Method, Request/Response DTO 구조를 설계하여 사용자에게 먼저 제안하고 확정받은 뒤 본 문서에 기록한다.

Base path: `/api/v1`

> ⚠️ 확인 필요: 실제 구현된 `DeliveryController`는 `/api/v1`이 아닌 `/api/deliveries`를 사용 중이다 (`backend/src/main/java/com/logistics/domain/delivery/presentation/DeliveryController.java`). 신규 도메인부터 `/api/v1`로 통일할지, 기존 구현에 맞춰 이 문서의 Base path를 바꿀지 결정이 필요하다.

## 핵심 API

| ID | Method | Path | 설명 |
|---|---|---|---|
| API-001 | POST | `/inbounds` | 입고 예정 등록 |
| API-002 | PATCH | `/inbounds/{id}/inspection` | 검수값 반영 |
| API-003 | POST | `/inbounds/{id}/complete` | 입고 완료 |
| API-004 | POST | `/outbound-plans` | 출고 계획 생성 |
| API-005 | POST | `/dispatches/candidates` | 차량 후보 조회 |
| API-006 | POST | `/dispatches` | 차량·기사 배차 확정 |
| API-007 | POST | `/dispatches/{id}/route/optimize` | 경로 계산/개선 |
| API-008 | PATCH | `/dispatches/{id}/status` | 배차 상태 변경 |
| API-009 | PATCH | `/dispatches/{id}/stops/{stopId}` | 경유지 상태 변경 |
| API-010 | GET | `/dispatches/{id}` | 배차 상세 조회(캐시) |
| API-011 | GET | `/dispatches` | 배차 목록/필터 |
| API-012 | GET | `/anomalies` | 이상 목록 |
| API-013 | GET | `/dashboard/summary` | 운영 요약 |
| API-014 | GET | `/events/logistics` | SSE 스트림 |

## 구현된 API — 입고/출고 (실제)

위 "핵심 API" 표는 `/api/v1` 기준의 초기 설계이고, 실제 구현은 `/api/deliveries`와 마찬가지로 `/api/v1` 없이 진행되었다. 입고→검수→출고 흐름은 아래와 같이 구현되어 있다.

| Method | Path | 설명 | Body |
|---|---|---|---|
| POST | `/api/inbounds` | 입고 요청 등록 (status=REQUESTED) | `{itemName, quantity, warehouseLocation}` |
| PATCH | `/api/inbounds/{id}/start` | 입고 처리 시작 (REQUESTED→IN_PROGRESS) | - |
| PATCH | `/api/inbounds/{id}/complete` | 검수 완료 (IN_PROGRESS→COMPLETED) | `{inspectedQuantity}` |
| GET | `/api/inbounds/{id}` | 입고 단건 조회 | - |
| GET | `/api/inbounds` | 입고 목록 조회 | - |
| GET | `/api/outbounds/available-inbounds` | 출고 대상 선정 (검수완료 + 가용수량>0 입고 목록) | - |
| POST | `/api/outbounds` | 출고 계획 생성 (여러 입고건 1:N 조합) | `{destination, items:[{inboundId, quantity}]}` |
| PATCH | `/api/outbounds/{id}/pick` | 피킹 시작 (REQUESTED→PICKING) | - |
| PATCH | `/api/outbounds/{id}/ship` | 출고 완료 (PICKING→SHIPPED) | - |
| GET | `/api/outbounds/{id}` | 출고 단건 조회 | - |
| GET | `/api/outbounds` | 출고 목록 조회 | - |

응답은 공통 `ApiResponse<T>` 포맷(`{success, message, data}`)을 사용하며, 위 "공통 오류 응답" 절의 형식과는 다르다 (실제 구현은 `ErrorResponse`: `{code, message, errors?}`).

`POST /api/outbounds`는 각 item마다 대상 `Inbound`가 `COMPLETED` 상태인지, `inspectedQuantity - 이미 배정된 수량 >= 요청 수량`인지 서버에서 검증하며, 위반 시 각각 `OB004`(미검수), `OB003`(수량 초과) 오류를 반환한다.

## 확정된 API — 차량·기사·배차 확정 (Day 3, ADR-006 반영)

배차 흐름: 출고 물량 → 차량 후보 조회 → 적재량 검증 → 차량 선택 → 기사 배정 → 배차 확정.

ADR-006에 따라 적재량은 `OutboundItem`에 직접 입력한다. 기존 `POST /api/outbounds`의 item 구조를 확장한다.

```
POST /api/outbounds
{ "destination": "...", "items": [{ "inboundId": 1, "quantity": 5, "weightKg": 12.5, "volumeM3": 0.8 }] }
```

| Method | Path | 설명 | Body |
|---|---|---|---|
| POST | `/api/vehicles` | 차량 등록 | `{vehicleNumber, vehicleType, maxWeightKg, maxVolumeM3, hubDistanceKm}` |
| GET | `/api/vehicles?status=AVAILABLE` | 차량 목록(상태 필터) | - |
| PATCH | `/api/vehicles/{id}/status?status=MAINTENANCE` | 차량 상태 변경(정비 등록/해제) | - |
| POST | `/api/drivers` | 기사 등록 | `{name}` |
| GET | `/api/drivers?status=AVAILABLE` | 기사 목록(상태 필터) | - |
| PATCH | `/api/drivers/{id}/status?status=OFF` | 기사 상태 변경(휴무 등록/해제) | - |
| POST | `/api/dispatches/candidates` | 차량 후보 조회(적재량 검증 포함) | 아래 참고 |
| POST | `/api/dispatches` | 차량·기사 배차 확정 | 아래 참고 |

**POST `/api/dispatches/candidates`**

```json
// Request
{ "outboundIds": [1, 2], "plannedAt": "2026-09-24T09:00:00" }

// Response (data)
{
  "totalWeightKg": 320.5,
  "totalVolumeM3": 4.2,
  "candidates": [
    { "vehicleId": 10, "vehicleNumber": "12가3456", "usedWeightRatio": 0.64, "usedVolumeRatio": 0.52, "score": 1.16, "reason": "잔여 적재율 양호" }
  ],
  "excluded": [
    { "vehicleId": 11, "vehicleNumber": "34나5678", "reasonCode": "OVER_CAPACITY", "reasonMessage": "부피 한도 초과 (4.2 > 3.8)" },
    { "vehicleId": 12, "vehicleNumber": "56다9012", "reasonCode": "VEHICLE_UNAVAILABLE", "reasonMessage": "정비중" }
  ]
}
```

**POST `/api/dispatches`**

```json
// Request
{ "vehicleId": 10, "driverId": 5, "outboundIds": [1, 2], "plannedAt": "2026-09-24T09:00:00" }

// Response (data)
{ "id": 100, "vehicleId": 10, "driverId": 5, "outboundIds": [1, 2], "plannedAt": "2026-09-24T09:00:00", "totalWeightKg": 320.5, "totalVolumeM3": 4.2, "status": "CONFIRMED" }
```

실패 시 `ErrorResponse {code, message, errors?}` 형식으로 `DISPATCH_OVER_CAPACITY`, `DISPATCH_VEHICLE_UNAVAILABLE`, `DISPATCH_DRIVER_SCHEDULE_CONFLICT`, `DISPATCH_DUPLICATE_ASSIGNMENT` 중 하나를 반환한다. 기존 `POST /api/dispatches`(자유 텍스트 `driverName`/`vehicleNumber` + waypoints 직접 입력) 방식은 이 스펙으로 대체된다.

**동시성/일정 모델**: `Vehicle.status`/`Driver.status`는 정비·휴무 같은 거시 상태만 표현하며, 개별 배차 점유 여부로 ASSIGNED로 전환하지 않는다(동일 차량·기사가 겹치지 않는 시간대에 여러 배차를 가질 수 있음). 배차 가능 여부는 계획 시각 기준 고정 길이(4시간) 창의 겹침으로 판단하며(`DispatchWindow`), 동시 확정 요청은 차량/기사 행에 대한 명시적(비관적) 잠금으로 직렬화한다. 출고 계획 중복 배정은 `dispatch_outbound_link` 테이블의 `outbound_id` unique 제약으로 최종 차단한다. `Dispatch` 엔티티에는 낙관적 락(`@Version`)이 있으나 위 잠금 전략과는 별개로 조회-수정 충돌 방지용으로만 존재한다.

## 확정된 API — 경로 최적화·배송 상태 (Day 4, ADR-008 반영)

ADR-008에 따라 `Outbound`에 `latitude`/`longitude`를 추가한다(출고 등록 시 필수 입력). 허브 좌표는 `application.yml`의 `hub.latitude`/`hub.longitude`(env override) 고정값이다. 거리는 Haversine(km)으로 계산한다.

```
POST /api/outbounds
{ "destination": "...", "latitude": 37.50, "longitude": 127.03, "items": [...] }
```

| Method | Path | 설명 | Body |
|---|---|---|---|
| POST | `/api/dispatches/{id}/route/optimize` | 허브+배송지 방문 순서 계산(NN+2-opt) | - |
| PATCH | `/api/dispatches/{id}/status` | 배차 상태 변경 | `{status, expectedVersion, actor?, description?}` |
| PATCH | `/api/dispatches/{id}/stops/{stopId}` | 경유지 상태 변경 | `{status}` |
| GET | `/api/dispatches/{id}` | 배차 상세(경로 `stops`, 상태 이력 `statusHistory` 포함) | - |

**POST `/api/dispatches/{id}/route/optimize`**

```json
// Response (data)
{
  "dispatchId": 100, "algorithm": "nearest-neighbor+2opt",
  "initialDistanceKm": 42.1, "optimizedDistanceKm": 35.7, "improvementRate": 0.152,
  "stops": [
    { "sequence": 0, "outboundId": null, "label": "허브", "latitude": 37.50, "longitude": 127.03, "distanceFromPreviousKm": 0.0, "status": "PENDING" },
    { "sequence": 1, "outboundId": 1, "label": "서울", "latitude": 37.55, "longitude": 126.97, "distanceFromPreviousKm": 8.2, "status": "PENDING" }
  ]
}
```

**PATCH `/api/dispatches/{id}/status`**: `DispatchStatus`는 `CONFIRMED → LOADED → IN_TRANSIT → COMPLETED` 순서만 허용(단계 건너뛰기·역행·COMPLETED 이후 변경 거부). `expectedVersion`이 현재 버전과 다르면 낙관적 잠금 충돌(409)로 거부. 모든 Stop이 `DELIVERED`가 아니면 `COMPLETED`로 전이할 수 없다. 변경마다 `DispatchStatusHistory`에 actor/이전상태/이후상태/시각/description을 기록한다.

**PATCH `/api/dispatches/{id}/stops/{stopId}`**: `RouteStop.status`는 `PENDING → ARRIVED → DELIVERED` 순서만 허용.

실패 시 `ErrorResponse`로 `DISPATCH_INVALID_STATUS_TRANSITION`, `DISPATCH_STALE_VERSION`, `DISPATCH_STOPS_NOT_DELIVERED`, `ROUTE_STOP_NOT_FOUND`, `ROUTE_STOP_INVALID_TRANSITION` 중 하나를 반환한다.

## 확정된 API — 이벤트·캐시·실시간 관제 (Day 5, ADR-009/010 반영)

| Method | Path | 설명 |
|---|---|---|
| GET | `/api/dispatches/{id}` | (기존과 동일) Redis cache-aside 적용. Key `dispatch:detail:{id}`, TTL 60초(`CACHE_DISPATCH_DETAIL_TTL_SECONDS` env로 조정) |
| GET | `/api/anomalies?status=OPEN` | 이상 목록(상태 필터) |
| PATCH | `/api/anomalies/{id}/status` | 이상 상태 변경(`OPEN→ACKNOWLEDGED→RESOLVED`) |
| GET | `/api/events/logistics` | SSE 스트림 (`text/event-stream`) |

**캐시 무효화**: `PATCH /dispatches/{id}/status`, `PATCH /dispatches/{id}/stops/{stopId}`, `POST /dispatches/{id}/route/optimize` 성공 시 해당 `dispatch:detail:{id}` 캐시를 즉시 삭제한다. Redis 장애 시(`CacheErrorHandler`가 예외를 삼킴) 캐시를 거치지 않고 DB로 폴백하며, 이 경우 요청은 계속 성공한다.

**이상 탐지 (ADR-009)**: 거부형(`OVER_CAPACITY`, `DRIVER_SCHEDULE_CONFLICT`, `DUPLICATE_ASSIGNMENT`, `INVALID_TRANSITION`)은 배차 확정/상태 변경 실패 시점에 동기적으로(REQUIRES_NEW 트랜잭션) 기록되며 Kafka를 거치지 않는다. 지연형(`STALLED_DISPATCH`)은 `IN_TRANSIT` 상태가 기준 시간(`DISPATCH_STALLED_THRESHOLD_MINUTES`, 기본 240분)을 초과하면 스케줄러가 생성한다. 동일 `fingerprint`(유형+배차ID)의 `OPEN` 이상이 이미 있으면 새로 만들지 않는다.

**Kafka 이벤트** (토픽 `logistics.dispatch.v1`, 배차 상태가 성공적으로 바뀐(커밋된) 시점에만 발행):

```json
{
  "eventId": "uuid", "eventType": "DISPATCH_IN_TRANSIT", "aggregateId": "100",
  "aggregateVersion": 3, "occurredAt": "2026-09-24T09:00:00Z", "traceId": "...",
  "payload": {"dispatchId": 100, "fromStatus": "LOADED", "toStatus": "IN_TRANSIT"}
}
```

- `eventType`: `DISPATCH_CONFIRMED`, `DISPATCH_LOADED`, `DISPATCH_IN_TRANSIT`, `DISPATCH_COMPLETED` 중 하나
- **HistoryConsumer**(`groupId=logistics-history`): `processed_event`로 멱등 처리 후 `dispatch_status_projection`(조회용 투영)을 갱신한다.
- **AnomalyConsumer**(`groupId=logistics-anomaly`): `processed_event`로 멱등 처리하고, 같은 aggregate의 이미 처리한 `aggregateVersion`보다 낮은 이벤트(역순 도착)는 건너뛴다.
- 두 Consumer 모두 역직렬화 실패와 업무 실패를 구분해 로그로 남기고, 무한 재시도하지 않는다(Spring Kafka 기본 재시도 3회 후 포기, DLQ는 범위 밖).
- DB 커밋 후 발행 방식이라 Kafka 발행 자체가 실패하면 이벤트가 유실될 수 있다(Outbox 패턴은 승인 없이 추가하지 않음, 알려진 한계로 기록).

**SSE**: `text/event-stream`. 배차 확정/상차/운행 시작/경유지 완료/배송 완료/이상 탐지 시점에 브로드캐스트한다. 15초 간격 heartbeat(주석 이벤트)로 연결을 유지하고, 전송 실패한 emitter는 즉시 제거한다. 이벤트 저장소로 사용하지 않는다(재연결 시 과거 이벤트 재전송 없음).

## 공통 오류 응답

```json
{
  "timestamp": "2026-09-21T12:00:00Z",
  "traceId": "...",
  "code": "DISPATCH_OVER_CAPACITY",
  "message": "차량 적재 한도를 초과했습니다.",
  "details": {"maxWeightKg": 1000, "requestedWeightKg": 1180}
}
```

## 중요 계약

- 생성 API는 `Idempotency-Key` 헤더를 받는다.
- `PATCH /dispatches/{id}/status`는 `expectedVersion`을 받아 동시 변경 충돌을 감지한다.
- 차량 후보 응답에는 선택 근거와 제외 사유를 함께 제공한다.
- 경로 최적화 응답에는 `algorithm`, `initialDistance`, `optimizedDistance`, `improvementRate`, `stops`를 포함한다.
- 목록은 페이지 크기 상한을 둔다.
- SSE 이벤트는 `eventId`, `type`, `aggregateId`, `occurredAt`, 최소 표시 데이터를 가진다.

## 이벤트 스키마

토픽: `logistics.dispatch.v1`

```json
{
  "eventId": "uuid",
  "eventType": "DISPATCH_STATUS_CHANGED",
  "aggregateId": "uuid",
  "aggregateVersion": 3,
  "occurredAt": "2026-09-21T12:00:00Z",
  "traceId": "...",
  "payload": {"from": "LOADED", "to": "IN_TRANSIT"}
}
```

Consumer는 `(consumerName, eventId)`로 중복 처리를 차단한다.

