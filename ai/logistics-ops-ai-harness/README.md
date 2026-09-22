# Logistics Ops AI Harness

물류센터의 입고·검수·출고 계획·차량/기사 배차·배송 경로 최적화·상차·배송 관제를 7일 동안 구현하기 위한 AI 코딩 에이전트용 개발 지시서다.

## 프로젝트 한 줄 설명

입·출고 물량과 차량 정보를 기반으로 배차 및 배송 경로를 최적화하고, 물류 처리 현황을 실시간으로 관리하는 물류 운영 시스템.

## 기술 스택

- Backend: Java 17 / Spring Boot 3.x / Spring Data JPA
- Frontend: React 18+ / TypeScript / Tailwind CSS
- Infra/Event: PostgreSQL / Redis / Apache Kafka / SSE / Docker Compose
- Test/Perf: JUnit 5 / k6

상세 근거는 `.ai/PROJECT.md`, `.ai/ARCHITECTURE.md` 참고.

## 문서 읽기 순서

1. `AGENTS.md`
2. `.ai/PROJECT.md`
3. `.ai/SCOPE.md`
4. `.ai/REQUIREMENTS.md`
5. `.ai/DOMAIN.md`
6. `.ai/ARCHITECTURE.md`
7. `.ai/DATABASE.md`
8. `.ai/API_SPEC.md`
9. `.ai/DEVELOPMENT_RULES.md`
10. `.ai/TEST_STRATEGY.md`
11. `.ai/DEVELOPMENT_PHASES.md`
12. `.ai/TASKS.md`
13. `docks/development/DAY_01_FOUNDATION.md`부터 날짜 순서대로 확인

## 7일 성공 기준

- 입고 완료 물량으로 출고 계획을 생성할 수 있다.
- 중량·부피·차량 상태를 검증해 차량과 기사를 배차할 수 있다.
- 좌표 기반 `Nearest Neighbor + 2-opt`로 배송 순서를 계산한다.
- 배차 완료부터 배송 완료까지 상태 전이를 강제한다.
- Kafka 이벤트로 이력·이상 탐지를 후속 처리한다.
- Redis 적용 전후 조회 성능을 k6로 비교한다.
- SSE로 관리자 화면에 상태 이벤트를 전달한다.
- Docker Compose로 핵심 실행 환경을 재현한다.

## 작업 요청 예시

```text
TASK-005를 수행하라.
먼저 AGENTS.md와 .ai 문서에서 관련 요구사항·도메인 규칙·API·완료 조건을 확인하라.
코드를 수정하기 전에 현재 저장소를 조사하고 영향 범위와 구현 계획을 보고하라.
승인 게이트에 해당하지 않으면 구현, 테스트, 빌드, diff 검토, 문서 갱신까지 수행하라.
완료 보고에는 실행한 명령과 PASS/FAIL 증거를 포함하라.
```

## 단계별 개발 문서

| 일차 | 문서 | 핵심 결과물 |
|---:|---|---|
| 1 | `docks/development/DAY_01_FOUNDATION.md` | 실행 기반, DB 초안, 공통 규약 |
| 2 | `docks/development/DAY_02_INBOUND_OUTBOUND.md` | 입고·검수·출고 계획 |
| 3 | `docks/development/DAY_03_FLEET_DISPATCH.md` | 차량 후보와 배차 확정 |
| 4 | `docks/development/DAY_04_ROUTE_DELIVERY.md` | 경로 최적화와 배송 상태 |
| 5 | `docks/development/DAY_05_EVENT_CACHE_REALTIME.md` | Kafka·Redis·SSE |
| 6 | `docks/development/DAY_06_UI_INTEGRATION_PERFORMANCE.md` | 운영 화면·통합·k6 |
| 7 | `docks/development/DAY_07_STABILIZATION_PORTFOLIO.md` | 안정화·문서·시연 자료 |
