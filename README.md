# 🚚 물류 통합 관리 시스템

> **Modular Monolith 기반의 주문 → 창고 작업 → 차량 인수 → 배송 → 고객 인도 → 반품 통합 물류 관리 시스템**

주문이 생성된 이후 재고 확인, 피킹, 포장, 상차, 배차, 배송, 배송 완료/실패, 반품까지 전체 물류 프로세스를 하나의 시스템에서 관리하고 상태와 수량을 추적합니다. 모든 상태 변경은 **감사로그**로 남고, Kafka · Redis · SSE를 통해 **대시보드에 실시간 반영**됩니다. (모노레포)

`ORD-001` 주문 하나가 시스템 전체를 관통합니다.

```
상품/창고/차량/기사 등록 → 입고·적치 → 재고 확보 → 주문 생성(재고 예약) → 출고 지시
→ 피킹 → 포장 → 상차 → 배차 → 배송중 → 배송완료 | 배송실패 → 반품(회수 → 반품입고 → 재고 복구)
```

## 🖥️ 화면 구성

| 메뉴 | 하위 화면 | 핵심 내용 |
|---|---|---|
| 📊 대시보드 | - | 오늘의 주문·피킹대기·포장대기·상차대기·배송중·완료·실패, 단계별 진행 현황, 차량 배송 현황, 최근 이벤트 (SSE 실시간) |
| 📦 주문관리 | 주문 목록 / 주문 상세 | 주문번호·고객명·상태·일자 검색, 상태 Badge, 진행 타임라인, 주문/피킹/상차/배송 수량, 이벤트 이력 |
| 🏭 창고관리 | 재고 현황 / 입고·적치 / 피킹·포장 | 현재·예약·가용 재고, 입고 → 적치, 피킹·포장 작업(탭) |
| 🚚 배송관리 | 상차관리 / 배차관리 / 배송현황 / 배송완료·실패 | 포장완료 주문 상차, 차량·기사 배차(적재량 검증), 차량별 진행률, 완료/실패 처리 |
| ↩️ 반품관리 | - | 회수요청 → 회수중 → 회수완료 → 반품입고 → 처리완료 |
| 📋 감사로그 | - | 누가 언제 어떤 상태를 바꿨는지(이전/변경 상태) 검색 |
| ⚙️ 기준정보 | 상품관리 / 창고·위치관리 / 차량·기사관리 | 상품, 창고→구역→위치 트리, 차량·기사 |

> 창고·위치관리에는 **3D 보기** 탭(Three.js)이 있어 위치 칸을 재고 상태 색(재고 없음/가용/예약/소진)으로 보여 주고, 상태가 바뀌면 SSE로 갱신됩니다. 나중에 추가: 스케줄(달력), 지도.

## ✅ 구현 현황

Day 1~3 범위(기준정보, 입고·재고, 주문~배송, 반품, 감사로그, 대시보드)가 모두 구현되어 있습니다. 백엔드는 자동 테스트(도메인 규칙, 서비스, API, 동시성, `ORD-001` E2E)로 검증했고, 실제 PostgreSQL·Redis·Kafka에서 전체 흐름과 SSE, Redis 장애 폴백을 확인했습니다.

## 🔁 주요 규칙

- **재고**: `가용재고 = 현재재고 - 예약재고`. 주문 시 예약, 피킹 시 차감, 반품입고 시 복구(파손 제외).
- **주문 상태**: 주문접수 → 출고대기 → 피킹중 → 피킹완료 → 포장완료 → 상차완료 → 배차완료 → 배송중 → 배송완료 | 배송실패
- **수량**: 피킹수량 = 요청수량, 인도수량 = 배송수량일 때만 완료 (부분 처리 없음)
- **배차**: 상차 → 배차 순서. 차량 적재량(kg) 초과, 사용 중인 차량·기사 중복 배정은 거부
- **감사로그**: 모든 상태 변경을 Kafka 이벤트로 발행 → 감사로그 기록 + 대시보드 캐시 무효화 + SSE 전송

## 🛠️ 기술 스택

- **Backend**: Java 17 / Spring Boot 3.x / Spring Data JPA
- **Frontend**: React 18+ / TypeScript / Vite / Tailwind CSS
- **Infra/Event**: PostgreSQL / Redis(대시보드 Cache-Aside) / Apache Kafka(상태 변경 이벤트) / SSE / Docker Compose
- **Test**: JUnit 5 (선택: k6)

## 📂 프로젝트 구조

```
Logistics-system/
├── backend/            Spring Boot 3.3 (Java 17, Gradle)              :8080
├── frontend/           React + Vite + TypeScript                     :5173
├── docker-compose.yml  개발용 인프라 (PostgreSQL, Redis, Kafka)
├── .ai/                설계 문서 (요구사항, 도메인, DB, API, 결정 기록 등)
├── ai/logistics-ops-ai-harness/  AI 에이전트 운영 계약(AGENTS.md)과 문서 읽기 순서
└── docks/development/  일차별(Day 1~3) 개발 지시서
```

## 📚 설계 문서

| 문서 | 내용 |
|---|---|
| [.ai/PROJECT.md](.ai/PROJECT.md), [.ai/SCOPE.md](.ai/SCOPE.md) | 목적, 범위, 제외 항목 |
| [.ai/REQUIREMENTS.md](.ai/REQUIREMENTS.md) | 화면 구조와 REQ/NFR |
| [.ai/DOMAIN.md](.ai/DOMAIN.md) | 도메인, 상태 머신, 재고·배차 규칙 |
| [.ai/ARCHITECTURE.md](.ai/ARCHITECTURE.md), [.ai/DATABASE.md](.ai/DATABASE.md) | 구조, 이벤트, 캐시, 테이블 |
| [.ai/API_SPEC.md](.ai/API_SPEC.md) | REST/SSE API |
| [.ai/DEMO_SCENARIO.md](.ai/DEMO_SCENARIO.md) | `ORD-001` 시연 시나리오 |
| [.ai/DECISION_LOG.md](.ai/DECISION_LOG.md) | 설계 결정(ADR)과 미결정 항목 |
| [.ai/DEVELOPMENT_PHASES.md](.ai/DEVELOPMENT_PHASES.md), [.ai/TASKS.md](.ai/TASKS.md) | 3일 개발 계획과 작업 목록 |

## 🚨 AI 개발 가이드 (에이전트 필독)

본 프로젝트는 엄격한 하네스 엔지니어링 지침에 따라 제어됩니다. 코드를 수정하거나 추가하기 전 반드시 루트의 [CLAUDE.md](CLAUDE.md)와 [ai/logistics-ops-ai-harness/AGENTS.md](ai/logistics-ops-ai-harness/AGENTS.md)를 먼저 정독하십시오.

- 모든 작업은 진행 전 **사용자에게 선보고 후 승인**을 받아야 합니다.
- 코드를 짜기 전 **API Spec(URI, Method, DTO)을 먼저 확정**해야 합니다 ([.ai/API_SPEC.md](.ai/API_SPEC.md)).
- 단일 파일은 **300~400자(약 30~50줄) 이하**를 유지해야 하며, 초과 시 분리 계획을 먼저 보고해야 합니다.
- 도메인 간 Repository 주입·Entity 직접 참조 금지. ID 참조, Application Service 호출, 이벤트로만 협력합니다.

## 🚀 빠른 시작 (데모)

```bash
docker compose up -d                 # 1. PostgreSQL / Redis / Kafka
cd backend && ./gradlew bootRun      # 2. 백엔드 :8080 (테이블은 자동 생성)
./scripts/demo-seed.sh               # 3. 데모 기준 데이터 (다른 터미널, 빈 DB에서 한 번만)
cd frontend && yarn install && yarn dev   # 4. 프런트 :5173
```

이후 [.ai/DEMO_SCENARIO.md](.ai/DEMO_SCENARIO.md) 순서대로 `ORD-001`을 진행하면 됩니다. 대시보드를 열어 두면 상태가 바뀔 때마다 SSE로 실시간 갱신됩니다.

- 이전 버전의 로컬 DB가 남아 있으면 새 테이블과 충돌합니다. `docker compose down -v`로 초기화한 뒤 다시 시작하세요(로컬 데이터가 삭제됩니다).
- 백엔드 테스트: `cd backend && ./gradlew test` (H2와 인메모리 캐시를 쓰므로 인프라 없이 실행됩니다)
- 프런트 검사: `cd frontend && yarn lint && yarn build`

## 개발 환경 구성

백엔드와 프론트는 Docker 없이 호스트에서 직접 실행하고, 인프라만 Docker로 띄웁니다.

```
IntelliJ (Spring Boot :8080) ──► Docker: PostgreSQL :5432 / Redis :6379 / Kafka :9092
React (:5173) ── REST / SSE ──► Spring Boot :8080
```

### 1. 인프라 실행 (Docker Desktop 실행 후)

```bash
docker compose up -d
docker compose ps
```

### 2. 백엔드

IntelliJ에서 `backend/`(또는 저장소 루트)를 열고 `MainApplication`을 실행합니다.

- 접속 기본값이 모두 `localhost`라 별도 환경변수는 필요 없습니다. 테이블은 `JPA_DDL_AUTO=update`(기본값)로 자동 생성됩니다.
- Swagger UI: http://localhost:8080/swagger-ui.html
- Health: http://localhost:8080/actuator/health

### 3. 프론트엔드

```bash
cd frontend
cp .env.example .env
yarn install
yarn dev
```

자세한 내용은 [backend/README.md](backend/README.md), [frontend/README.md](frontend/README.md)를 참고하세요.

## 한계 (의도적 단순화)

- 로그인/인증 없음(전 엔드포인트 PermitAll). 감사로그 사용자는 `X-Actor` 헤더로 받습니다.
- 부분 피킹·부분 출고·분할 적치·재배송·주문 취소 없음.
- 경로 최적화, 이상 탐지 없음.
- Kafka는 커밋 후 발행 방식입니다. 발행에 실패하면 로그만 남고 재처리(Outbox)는 구현하지 않아 드물게 감사로그·대시보드 갱신이 누락될 수 있습니다.
- 감사로그와 대시보드 Consumer는 서로 다른 그룹이라 "최근 이벤트" 조회가 방금 발생한 이벤트보다 잠깐 늦을 수 있습니다(화면은 SSE로 받은 이벤트를 바로 추가해 보완합니다).
- SSE 구독자는 서버 메모리에서 관리하는 단일 인스턴스 전제입니다. 연결이 끊기면 브라우저가 자동 재연결합니다.
- 스키마는 Flyway 없이 `JPA_DDL_AUTO=update`로 생성합니다(로컬 개발용). 컬럼 삭제·타입 변경은 반영되지 않습니다.
- 재고 조회 등 일부 목록은 데모 규모를 가정해 메모리에서 거르며, 주문·감사로그 목록은 최대 개수(각 20/100건)만 반환합니다.
- 스케줄·지도, 성능 테스트(k6)는 구현하지 않았습니다.
- 3D 창고는 위치 코드(`A-01-02` → 열 1, 단 2)를 해석해 배치합니다. 해석할 수 없는 코드는 격자로 배치하며, 실제 크기·좌표 데이터는 없습니다.

## 인프라 정리

```bash
docker compose down        # 컨테이너만 중지 (데이터 유지)
```
