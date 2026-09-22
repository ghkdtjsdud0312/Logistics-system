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

