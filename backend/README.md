# logistics-system_back

물류 담당자 입장에서의 물류 통합 관리 시스템 - 백엔드

## 기술 스택
- Backend: Java 17, Spring Boot 3.3
- ORM: Spring Data JPA
- DB: PostgreSQL
- Cache: Redis
- Event: Apache Kafka
- 실시간: SSE
- API 문서: Swagger / OpenAPI (springdoc)
- 테스트: JUnit5, Spring Boot Test
- 실행: Docker / Docker Compose
- 빌드: Gradle

## 시작하기

### 1. 인프라 실행 (Postgres / Redis / Kafka)
저장소 루트에서 실행합니다. (`docker-compose.yml`은 루트에 있습니다)
```bash
cd ..
docker compose up -d
```

### 2. 애플리케이션 실행
```bash
./gradlew bootRun
```
- 최초 실행 시 `./gradlew wrapper --gradle-version 8.10` 로 Gradle Wrapper를 생성해 주세요. (`gradlew`, `gradle/wrapper/gradle-wrapper.jar` 포함)
- Swagger UI: http://localhost:8080/swagger-ui.html

### 3. 테스트
```bash
./gradlew test
```

### 4. 데모 데이터
빈 DB에서 앱을 띄운 뒤 저장소 루트에서 `./scripts/demo-seed.sh`를 실행하면 상품·창고·차량·기사와 재고가 만들어집니다.

### 테스트 구성
- 도메인 단위 테스트: 상태 전이, 재고 규칙, 수량 검증
- 서비스·API 테스트(H2): 흐름, 경계값, 오류 응답 (`domain/*`)
- 동시성 테스트: 동시 주문의 초과 예약 방지, 같은 배송의 이중 배차 방지
- E2E 테스트(`e2e/OrderJourneyE2ETest`): `ORD-001` 성공 흐름, 배송 실패 → 반품 → 재고 복구
- 테스트 프로파일에서는 Kafka 발행과 Consumer를 끄고(`app.event.kafka-enabled=false`) 인메모리 캐시를 사용합니다.

## 도메인 구성 (목표 구조)
주문 하나가 재고·창고 작업·상차·배차·배송·반품을 관통합니다. 도메인 간에는 Repository 주입과 JPA 연관 없이 ID 참조, Application Service 호출, Kafka 이벤트로만 협력합니다.
```
src/main/java/com/logistics/
├── MainApplication.java
├── global/
│   ├── config/     # Swagger, Redis, Kafka, Security(PermitAll), CORS 설정
│   ├── error/      # 공통 예외 처리 (ErrorCode, BusinessException, GlobalExceptionHandler)
│   ├── common/     # 공통 응답(ApiResponse), BaseTimeEntity
│   ├── event/      # 상태 변경 이벤트 발행 (커밋 후)
│   └── sse/        # SSE 브로드캐스터
└── domain/
    ├── master/     # 상품, 창고·구역·위치
    ├── vehicle/    # 차량
    ├── driver/     # 기사
    ├── inbound/    # 입고·적치
    ├── inventory/  # 재고 (현재/예약/가용)
    ├── order/      # 주문, 주문 상태
    ├── warehouse/  # 피킹·포장 작업
    ├── loading/    # 상차, Shipment
    ├── dispatch/   # 배차
    ├── delivery/   # 배송 시작·완료·실패, 배송현황
    ├── returns/    # 반품
    ├── audit/      # 감사로그 (Kafka Consumer)
    └── dashboard/  # 대시보드 집계 (Redis 캐시, SSE)
```
> 이전 도메인(`outbound`, `anomaly`, `dispatch`, `delivery`, `inbound`)은 삭제했고 `vehicle`, `driver`만 남아 있습니다. 나머지는 Day 1~3에 새로 구현합니다. 진행 상황은 `.ai/TASKS.md`를 참고하세요.

각 도메인 모듈은 DDD 관점의 4계층으로 구성됩니다.
- `presentation/` — Controller, Request/Response DTO (비즈니스 로직 금지)
- `application/`  — Service (유스케이스 흐름 제어)
- `domain/`       — Entity, VO, Repository 포트(인터페이스), 상태 전이 등 핵심 규칙
- `infrastructure/` — JPA Repository 구현체, Kafka Producer/Consumer, Redis 등 외부 기술 연동

## API
REST는 `/api` 하위, SSE는 `GET /api/events/logistics`입니다. 전체 명세는 [../.ai/API_SPEC.md](../.ai/API_SPEC.md)를 참고하세요.

## IDE 플러그인 (IntelliJ)
아래 플러그인을 Marketplace에서 검색하여 설치해 주세요. (플러그인 마다 정확한 배포 ID가 달라 프로젝트 설정 파일로 자동화하지 않았습니다)
- Claude Code [Beta]
- Claude Code with GUI
- Grep Console
- Jakarta EE: Web Services (JAX-WS)
- PlantUML integration (plantuml4idea)
- SonarQube for IDE

## 참고
- `.env` / `application-local.yml` 등 민감 정보가 담긴 파일은 `.gitignore`에 포함되어 있습니다.
- 루트 `docker-compose.yml`의 `backend` 서비스는 기본적으로 주석 처리되어 있습니다. 컨테이너로 앱까지 함께 띄우려면 주석을 해제하세요.
