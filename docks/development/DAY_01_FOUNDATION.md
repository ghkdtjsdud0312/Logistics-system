# Day 1 — 프로젝트 기반과 개발 계약

## 1. 오늘의 목표

코드를 많이 작성하는 날이 아니라 이후 6일간 변경의 기준이 될 실행 환경, 모듈 경계, 데이터베이스 초안, 오류 규약을 확정한다. 하루 종료 시 백엔드와 데이터베이스가 실제로 연결되고 반복 실행 가능한 상태여야 한다.

## 2. 관련 범위

- 관련 요구사항: `REQ-023`, `REQ-024`, `NFR-001`, `NFR-004`, `NFR-005`
- 관련 작업: `TASK-001`, `TASK-002` 일부
- 선행조건: 없음
- 범위 밖: 입고·배차 비즈니스 기능, Kafka Consumer, UI 구현

## 3. 확정할 기술 기준

- Java 17, Spring Boot 3.x, Gradle
- PostgreSQL과 Flyway
- Redis와 Kafka는 Compose에 정의하되 오늘 업무 로직에 연결하지 않는다.
- React + TypeScript + Vite의 실행 뼈대만 만든다.
- 환경별 설정은 `local`, `test`로 분리한다.
- Testcontainers로 PostgreSQL 통합 테스트 기반을 준비한다.

## 4. 권장 저장소 구조

```text
project-root/
├── backend/
├── frontend/
├── k6/
├── docs/
├── docker-compose.yml
├── .env.example
├── AGENTS.md
└── .ai/
```

백엔드는 기능 중심 패키지 `inbound`, `outbound`, `fleet`, `dispatch`, `route`, `monitoring`, `common`을 사용한다. 기능 구현 전 빈 계층 파일을 대량 생성하지 않는다.

## 5. 세부 개발 내용

### 5.1 Backend 초기화

- Web, Validation, Data JPA, Actuator, PostgreSQL, Flyway, Testcontainers 의존성 구성
- `/actuator/health` 활성화
- 애플리케이션 시간대와 JSON 시간 형식을 UTC로 통일
- 테스트 가능한 `Clock` Bean 구성
- 로컬 비밀값을 제외한 `.env.example` 제공

### 5.2 공통 API 규약

공통 오류 응답은 `timestamp`, `traceId`, `code`, `message`, `details`를 가진다. Validation, 리소스 없음, 도메인 규칙 위반, 동시성 충돌을 서로 다른 오류 코드와 HTTP 상태로 구분한다.

### 5.3 추적과 로그

- 요청에 추적 ID가 있으면 재사용하고 없으면 생성한다.
- 응답 헤더와 로그에 같은 추적 ID를 남긴다.
- 로그에 전체 주소, 연락처, 환경변수 값을 출력하지 않는다.

### 5.4 데이터베이스 초안

`DATABASE.md`의 테이블을 기준으로 최초 Flyway 마이그레이션을 작성한다. 아직 구현하지 않은 Aggregate도 관계와 제약조건을 검토하되, 추측성 컬럼은 추가하지 않는다.

### 5.5 실행 환경

Docker Compose에 PostgreSQL, Redis, Kafka를 정의한다. healthcheck와 영속 볼륨을 설정하고, 애플리케이션은 로컬 JVM에서 실행해도 인프라에 접속할 수 있게 한다.

## 6. 테스트와 검증

- Spring Context 기동 테스트
- PostgreSQL Testcontainer 연결과 Flyway 적용 테스트
- 잘못된 요청에 대한 공통 오류 응답 테스트
- health endpoint 확인
- Backend 빌드와 Frontend 기본 빌드
- Compose 설정 유효성 확인

## 7. 산출물

- 실행 가능한 Backend/Frontend 기본 프로젝트
- `docker-compose.yml`, `.env.example`
- 최초 Flyway 마이그레이션
- 공통 오류 응답과 예외 처리기
- 추적 ID 필터와 구조화 로그 기본 설정
- 로컬 실행 방법 초안

## 8. 완료 기준

- [ ] 빈 환경에서 문서대로 인프라와 Backend를 실행할 수 있다.
- [ ] Flyway가 자동 적용되고 재실행해도 오류가 없다.
- [ ] health가 DB 연결 상태를 반영한다.
- [ ] 테스트와 빌드가 통과한다.
- [ ] 비밀값이 저장소에 포함되지 않는다.
- [ ] 기술 스택이나 DB 계약의 미결정 사항이 기록되어 있다.

## 9. 다음 단계 인계

Day 2에서는 확정된 DB와 공통 규약 위에서 Inbound와 OutboundPlan을 구현한다. 오늘 생성한 임시 Entity나 컬럼이 Day 2 도메인 규칙과 충돌하지 않는지 시작 전에 다시 확인한다.

