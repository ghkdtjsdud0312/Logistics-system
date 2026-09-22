# 🚚 물류 입·출고 및 배차 최적화 시스템

> **Modular Monolith 기반의 물류 운영 전반 관리 및 배송 경로 최적화 시스템**

물류센터의 입고부터 출고, 차량 배차, Nearest Neighbor 및 2-opt 기반 배송 경로 최적화, Kafka와 SSE를 활용한 실시간 배송 관제까지 물류 운영 전반을 관리하는 시스템입니다. (모노레포)

## 🛠️ 기술 스택

- **Backend**: Java 17 / Spring Boot 3.x / Spring Data JPA
- **Frontend**: React 18+ / TypeScript / Tailwind CSS
- **Infra/Event**: PostgreSQL / Redis / Apache Kafka / SSE / Docker Compose
- **Test/Perf**: JUnit 5 / k6

## 📂 프로젝트 구조

```
Logistics-system/
├── backend/            Spring Boot 3.3 (Java 17, Gradle)              :8080
├── frontend/           React + Vite + TypeScript                     :5173
├── docker-compose.yml  개발용 인프라 (PostgreSQL, Redis, Kafka)
├── .ai/                시스템 설계 및 아키텍처 규칙 문서
├── ai/logistics-ops-ai-harness/  AI 에이전트 운영 계약(AGENTS.md)과 문서 읽기 순서
└── docks/development/  일차별(Day 1~7) 개발 지시서
```

## 🚨 AI 개발 가이드 (에이전트 필독)

본 프로젝트는 엄격한 하네스 엔지니어링 지침에 따라 제어됩니다. 코드를 수정하거나 추가하기 전 반드시 루트의 [CLAUDE.md](CLAUDE.md)와 [ai/logistics-ops-ai-harness/AGENTS.md](ai/logistics-ops-ai-harness/AGENTS.md)를 먼저 정독하십시오.

- 모든 작업은 진행 전 **사용자에게 선보고 후 승인**을 받아야 합니다.
- 코드를 짜기 전 **API Spec(URI, Method, DTO)을 먼저 확정**해야 합니다 ([.ai/API_SPEC.md](.ai/API_SPEC.md)).
- 단일 파일은 **300~400자(약 30~50줄) 이하**를 유지해야 하며, 초과 시 분리 계획을 먼저 보고해야 합니다.

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

- 접속 기본값이 모두 `localhost`라 별도 환경변수는 필요 없습니다.
- 빈 DB에서는 `JPA_DDL_AUTO=validate` 환경변수가 필요합니다. (기본값 `validate`)
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

## 인프라 정리

```bash
docker compose down        # 컨테이너만 중지 (데이터 유지)
```
