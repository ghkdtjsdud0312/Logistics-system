# Logistics Ops AI Harness

주문 생성부터 재고 확인, 피킹, 포장, 상차, 배차, 배송, 완료/실패, 반품까지의 물류 전 과정을 3일(2026-09-25 ~ 09-27) 동안 구현하기 위한 AI 코딩 에이전트용 개발 지시서다.

## 프로젝트 한 줄 설명

주문 하나가 창고 작업과 차량 배송을 거쳐 고객에게 인도될 때까지의 상태와 수량을 추적하는 물류 통합 관리 시스템.

## 기술 스택

- Backend: Java 17 / Spring Boot 3.x / Spring Data JPA
- Frontend: React 18+ / TypeScript / Tailwind CSS
- Infra/Event: PostgreSQL / Redis / Apache Kafka / SSE / Docker Compose
- Test: JUnit 5 (선택: k6)

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
13. `.ai/DECISION_LOG.md`, `.ai/DEMO_SCENARIO.md`
14. `docks/development/DAY_01_FOUNDATION_MASTER_INVENTORY.md`부터 날짜 순서대로 확인

## 3일 성공 기준

- 기준정보를 등록하고 입고·적치로 재고(현재/예약/가용)를 만들 수 있다.
- `ORD-001`이 주문접수부터 배송완료까지 상태 전이 규칙대로 이동하고 재고·수량이 정합하다.
- 상차 → 배차(적재량 검증) → 배송 시작 → 완료/실패가 동작한다.
- 배송 실패 시 반품이 생성되고 회수 후 재고가 규칙대로 복구된다.
- 모든 상태 변경이 Kafka 이벤트로 감사로그에 남는다.
- 대시보드가 Redis 캐시와 SSE로 실시간 갱신된다.
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
| 1 (금) | `docks/development/DAY_01_FOUNDATION_MASTER_INVENTORY.md` | 기반 정리, 기준정보, 입고·적치, 재고 |
| 2 (토) | `docks/development/DAY_02_ORDER_TO_DELIVERY.md` | 주문 → 피킹 → 포장 → 상차 → 배차 → 배송 |
| 3 (일) | `docks/development/DAY_03_RETURN_AUDIT_DASHBOARD.md` | 반품, 감사로그, 대시보드(SSE), 마무리 |
