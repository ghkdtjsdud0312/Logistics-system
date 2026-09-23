# TASKS

## 작업 백로그

| Task | Day | 연결 요구사항 | 완료 산출물 |
|---|---:|---|---|
| TASK-001 프로젝트/인프라 초기화 | 1 | REQ-023, REQ-024 | 앱 실행, Compose, health, 오류 계약 |
| TASK-002 스키마와 마이그레이션 | 1 | REQ-001~019 | Flyway, Entity, Repository smoke test |
| TASK-003 입고 유스케이스 | 2 | REQ-001~003 | 등록/검수/완료 API와 테스트 |
| TASK-004 출고 계획 | 2 | REQ-004~006 | 계획 생성, 가용량 검증 |
| TASK-005 차량 후보 | 3 | REQ-007~009, 011 | 후보·제외 사유·경계 테스트 |
| TASK-006 배차 확정 | 3 | REQ-006, 008, 010 | 차량/기사/물량 연결, 충돌 테스트 |
| TASK-007 경로 최적화 | 4 | REQ-012~014 | NN+2-opt, 거리 비교, 결정성 테스트 |
| TASK-008 배송 상태/이력 | 4 | REQ-015~016 | 상태 머신, 경유지, 이력 |
| TASK-009 Kafka 후속 처리 | 5 | REQ-017, 019, NFR-003 | 이벤트, Consumer, 멱등성 |
| TASK-010 Redis 캐시 | 5 | REQ-020~021 | cache-aside, TTL, 무효화 |
| TASK-011 SSE 관제 | 5 | REQ-018 | 스트림, 재연결 기본 처리 |
| TASK-012 React 운영 화면 | 6 | REQ-001~019 | 핵심 3~5 화면 |
| TASK-013 통합/성능 검증 | 6 | REQ-022~024 | E2E, k6, Compose |
| TASK-014 포트폴리오 정리 | 7 | 전체 | README, 다이어그램, 결과표, 데모 |
| TASK-015 입고 실측 적재량 마이그레이션 (백로그) | - | ADR-006 | Inbound에 weightKg/volumeM3 실측 입력, Outbound가 inboundId로 조회하는 인터페이스 설계, OutboundItem 직접입력값 대체 |

## Task 실행 카드

각 Task 시작 전에 아래를 복사해 채운다.

```markdown
### TASK-XXX

- 목표:
- 연결 REQ:
- 선행 Task:
- 변경 예상 영역:
- 범위 밖:
- Acceptance Criteria:
  - [ ]
- 필수 테스트:
  - [ ] 정상
  - [ ] 경계
  - [ ] 오류
- 검증 명령:
- 문서 갱신:
- 결과/증거:
```

## 핵심 Task별 추가 완료 조건

### TASK-005

- 차량 중량과 부피를 모두 검증한다.
- 가용하지 않은 차량의 제외 사유를 반환한다.
- 같은 입력의 후보 정렬 결과가 결정적이다.

### TASK-006

- 기사 일정 충돌과 물량 중복을 차단한다.
- 동시 요청에서 이중 배차가 발생하지 않는다.
- 실패 시 부분 배차 데이터가 남지 않는다.

### TASK-005/006 진행 상태 (2026-09-23)

Vehicle/Driver 도메인, `POST /api/dispatches/candidates`, `POST /api/dispatches`(확정) 구현 완료. 적재량은 ADR-006(옵션 A), 일정 겹침 판단은 ADR-007(고정 4시간 창 + 비관적 잠금) 참고. 테스트: `DispatchCandidateServiceTest`, `DispatchConfirmServiceTest`, `DispatchConfirmConcurrencyTest`.

### TASK-007

- 모든 배송지를 한 번씩 포함한다.
- 최적화 거리가 초기 거리보다 길지 않다.
- 알고리즘 입력/결과와 단위를 문서화한다.

### TASK-007/008 진행 상태 (2026-09-23)

허브 좌표(ADR-008) + Outbound 좌표를 입력으로 Nearest Neighbor(허브 고정, 2-opt로 개선, Haversine km) 경로 계산 구현. `POST /dispatches/{id}/route/optimize`, `PATCH /dispatches/{id}/status`(expectedVersion 낙관적 잠금 + 이력 기록), `PATCH /dispatches/{id}/stops/{stopId}` 완료. DispatchStatus에 LOADED 추가(`CONFIRMED→LOADED→IN_TRANSIT→COMPLETED`), RouteStopStatus(`PENDING→ARRIVED→DELIVERED`) 모두 단계 건너뛰기/역행 차단, COMPLETED는 모든 Stop이 DELIVERED여야 허용. 테스트: `WaypointTest`, `TwoOptRouteImproverTest`, `RouteOptimizationServiceTest`, `DispatchStatusServiceTest`, `RouteStopServiceTest`.

### TASK-009

- 중복 eventId를 재처리해도 결과가 한 번만 반영된다.
- 역직렬화 실패와 업무 실패를 구분해 로그로 남긴다.
- 실패를 무한 재시도하지 않는다.

### TASK-013

- 캐시 OFF/ON 조건 외 환경을 동일하게 유지한다.
- 성능 수치와 함께 머신/데이터/VU/기간을 기록한다.
- 실패한 요청과 병목 원인을 숨기지 않는다.

