# logistics-system_back

물류 담당자 입장에서의 물류 입·출고 및 배차 최적화 시스템 - 백엔드

## 기술 스택
- Backend: Java 17, Spring Boot 3.3
- ORM: Spring Data JPA
- DB: PostgreSQL
- Cache: Redis
- Event: Apache Kafka
- 실시간: SSE
- API 문서: Swagger / OpenAPI (springdoc)
- 테스트: JUnit5, Spring Boot Test
- 성능 테스트: k6
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

### 4. 성능 테스트 (k6)
```bash
k6 run k6/inbound-load-test.js
```

## 폴더 구조 (DDD 계층 구조)
```
src/main/java/com/logistics/
├── MainApplication.java
├── global/
│   ├── config/     # Swagger, Redis, Kafka, Security, CORS 설정
│   ├── error/      # 공통 예외 처리 (ErrorCode, BusinessException, GlobalExceptionHandler)
│   └── common/     # 공통 응답(ApiResponse), BaseTimeEntity
└── domain/
    ├── inbound/    # 입고 모듈
    ├── outbound/   # 출고 모듈
    ├── dispatch/   # 배차 모듈 (Nearest Neighbor 알고리즘)
    └── delivery/   # 배송/관제 모듈 (SSE + Kafka)
```

각 도메인 모듈은 DDD 관점의 4계층으로 구성됩니다.
- `presentation/` — Controller, Request/Response DTO
- `application/`  — Service (유스케이스), 이벤트 리스너
- `domain/`       — Entity, VO, Repository 포트(인터페이스), 도메인 서비스(알고리즘 등)
- `infrastructure/` — JPA Repository 구현체, Kafka Producer, SSE Emitter 저장소 등 외부 기술 연동

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
