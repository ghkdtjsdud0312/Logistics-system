# Day 5 — 이벤트·캐시·실시간 관제

## 1. 오늘의 목표

완성된 동기 업무 흐름에 Kafka, Redis, SSE를 필요한 위치에만 연결한다. 기술을 장식처럼 추가하지 않고, 후속 처리 분리·조회 부하 완화·관제 화면 갱신이라는 목적을 각각 검증한다.

## 2. 관련 범위

- 요구사항: `REQ-017`~`REQ-021`, `NFR-003`
- 작업: `TASK-009`, `TASK-010`, `TASK-011`
- 선행조건: 상태 변경의 단일 진입점과 안정적인 배차 상세 API
- 범위 밖: Exactly-once 보장 주장, 대규모 이벤트 플랫폼, WebSocket

## 3. Kafka 이벤트

토픽은 `logistics.dispatch.v1`을 사용한다. 이벤트 Envelope는 eventId, eventType, aggregateId, aggregateVersion, occurredAt, traceId, payload를 가진다.

### Consumer

- History Consumer: 상태 변경 이력 또는 조회용 투영 갱신
- Anomaly Consumer: 금지 전이 시도, 정체 기준 등 이상 평가

각 Consumer는 `(consumerName, eventId)`를 기록해 동일 이벤트 재수신 시 업무 결과를 중복 생성하지 않는다.

### 실패 정책

- 역직렬화 오류와 업무 오류를 구분한다.
- 무한 재시도를 피한다.
- MVP 재시도 횟수와 실패 이벤트 처리 방식을 문서화한다.
- DB 커밋 후 발행 방식의 손실 가능성을 한계로 기록한다. Outbox는 승인 없이 추가하지 않는다.

## 4. Redis Cache

대상은 `GET /api/v1/dispatches/{id}`로 제한한다.

- cache-aside
- Key: `dispatch:detail:{id}`
- TTL: 기본 60초, 환경설정 가능
- 배차, 경로, Dispatch/Stop 상태가 변경되면 해당 키 삭제
- Redis 장애 시 DB 조회로 기능 유지
- null 또는 오류 응답은 캐시하지 않음

캐시 객체의 스키마 변경과 직렬화 문제를 고려해 버전 필드를 둘 수 있다.

## 5. SSE

- Endpoint: `GET /api/v1/events/logistics`
- 이벤트: 배차 확정, 상차 완료, 배송 시작, 경유지 완료, 배송 완료, 이상 탐지
- heartbeat로 연결 유지
- 느린 클라이언트와 끊긴 emitter 정리
- SSE를 영구 이벤트 저장소로 사용하지 않음

## 6. 이상 탐지

- OVER_CAPACITY, INVALID_TRANSITION, DRIVER_SCHEDULE_CONFLICT, DUPLICATE_ASSIGNMENT
- IN_TRANSIT 상태가 설정된 기준 시간을 초과하면 STALLED_DISPATCH
- 동일 원인의 중복 알림은 fingerprint로 억제
- 이상은 OPEN, ACKNOWLEDGED, RESOLVED 상태를 가질 수 있으나 UI 처리까지 복잡하게 확장하지 않는다.

## 7. 테스트

- 동일 Kafka 이벤트 두 번 소비 시 결과 한 건
- 역순 aggregateVersion 처리 정책
- Consumer 실패 후 재시도
- 캐시 miss→DB→저장, hit→DB 미조회
- 상태/경로 변경 후 캐시 삭제
- Redis 장애 시 DB fallback
- SSE 연결, 이벤트 수신, 연결 종료 정리
- 정체 기준 시각 테스트는 주입된 Clock 사용

## 8. 산출물

- 이벤트 DTO와 producer
- 두 개의 Consumer와 processed_event 저장
- Redis 설정, 캐시 서비스, 무효화
- SSE emitter 관리와 이벤트 변환
- 이상 저장/조회 기초
- 통합 테스트와 장애 한계 문서

## 9. 완료 기준

- [ ] 한 번의 상태 변경이 중복 이력 없이 후속 처리된다.
- [ ] 캐시 데이터가 상태 변경 뒤 오래 남지 않는다.
- [ ] Redis 장애가 핵심 조회 기능을 중단시키지 않는다.
- [ ] 브라우저 또는 테스트 클라이언트가 SSE 이벤트를 받는다.
- [ ] 전체 테스트와 빌드가 통과한다.

## 10. 다음 단계 인계

Day 6 화면에서 사용할 API와 이벤트 형식이 오늘 종료 시 고정되어야 한다. 프론트 구현 중 계약을 즉흥 변경하지 않도록 OpenAPI와 예시 응답을 갱신한다.

