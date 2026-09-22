# 프로젝트 하네스 지침: 물류 입·출고 및 배차 최적화 시스템

## 🚨 AI 행동 통제 핵심 원칙 (Absolute Rules)
1. **선보고 후진행 (Human-in-the-Loop)**: 모든 작업(설계 변경, 인프라 구축, 코드 작성 등)은 다음 단계로 넘어가기 전, 현재까지의 진행 상황과 다음 계획을 사용자에게 먼저 보고하고 **명시적인 승인**을 얻은 후에만 진행하십시오. 절대 독단적으로 다음 단계의 코드를 생성하지 마십시오.
2. **API Specification 선확정**: 어떤 비즈니스 기능을 구현하든, 코드를 작성하기 전에 반드시 필요한 **API의 URI, HTTP Method, Request/Response DTO 구조**를 설계하여 사용자에게 먼저 제안하고 확정받으십시오. 확정된 스펙은 즉시 `.ai/API_SPEC.md`에 기록되어야 합니다.
3. **Modular Monolith 의존성 제약**: 아키텍처는 Modular Monolith 구조를 엄격히 유지하며 향후 MSA로 변경하지 않습니다. 도메인 간의 강결합을 막기 위해 타 도메인의 엔티티를 직접 객체 참조(Join)하거나 타 도메인의 Repository를 직접 주입받지 마십시오. 오직 ID 참조 또는 이벤트를 통해서만 협력해야 합니다.
4. **인증 프리패스 유지**: `spring-boot-starter-security`가 백엔드 의존성에 포함되어 있지만, 개발 편의성을 위해 모든 엔드포인트는 로그인/인증 없이 통과하도록 `PermitAll` 설정을 최우선으로 적용하십시오.
5. **엄격한 파일 크기 제약 및 모듈화 (Max Code Length & Reusability)**:
   - 단일 소스 코드 파일(Java, TypeScript, TSX 모두 포함)의 길이는 공백을 포함하여 **300~400자(작성 라인 기준 약 30~50줄 내외)를 초과하지 않는 것을 원칙**으로 합니다.
   - 코드가 길어지거나 비대해질 조짐이 보이면 즉시 기능을 쪼개어 재사용 가능한 작은 컴포넌트(`components/`), 커스텀 훅(`hooks/`), pure function 유틸(`utils/`), 또는 작은 도메인 서비스로 분리하십시오.
   - ⚠️ **[초과 시 절대 규칙]** 만약 비즈니스 로직의 특성상(예: 최적화 알고리즘 등) 한 파일이 300~400자를 넘길 수밖에 없는 상황이 발생하면, **코드를 작성하기 전에 반드시 사용자에게 상황을 설명하고, 어떻게 분리할 것인지 계획을 보고하여 명시적인 승인을 얻은 후에만 진행**하십시오.

---

## 📂 제1단계: 설계 문서화 선행 요구사항
실제 자바/리액트 코드를 구현하기 전, `.ai/` 디렉터리에 아래 11개 문서 생성을 완료하고 사용자에게 일관성 검토 보고서를 제출하여 승인을 받으십시오. 승인 전에는 프로젝트 빈 뼈대 외의 도메인 코드를 작성할 수 없습니다.
* 생성 문서: `PROJECT.md`, `REQUIREMENTS.md`, `ARCHITECTURE.md`, `DOMAIN.md`, `DATABASE.md`, `API_SPEC.md`, `DEVELOPMENT_RULES.md`, `TEST_STRATEGY.md`, `DEVELOPMENT_PHASES.md`, `TASKS.md`, `DECISION_LOG.md`
* 추측이 필요한 부분은 임의로 결정하지 말고 `DECISION_LOG.md`에 '결정이 필요한 항목'으로 기록하십시오. 문서 간 내용 충돌 시 임의 수정하지 말고 즉시 사용자에게 보고하십시오.

> **현재 상태**: 위 11개 문서는 이미 `.ai/`에 생성되어 있고, 미결정 항목은 `.ai/DECISION_LOG.md`의 "미결정 항목" 절에 기록되어 있습니다. 도메인 코드도 이미 일부(입고/출고/배차/배송) 구현이 시작된 상태이므로, 새로 합류하는 에이전트는 이 절의 "코드 작성 금지"를 문자 그대로 적용하지 말고 `.ai/TASKS.md`의 진행 상태를 먼저 확인하십시오.

---

## 💻 제2단계: 백엔드 초기 세팅 규칙 (`backend/`)

### 1. 확장 의존성 및 기술 스택 (dependencies)
- **기본/웹**: `spring-boot-starter-web`, `spring-boot-starter-validation`
- **DB/JPA**: `spring-boot-starter-data-jpa`, `postgresql` (런타임)
- **보안**: `spring-boot-starter-security` (`SecurityConfig.java`에서 전면 해제 필수)
- **인프라**: `spring-boot-starter-data-redis` (Cache-Aside용), `spring-kafka` (배송 상태 변경 이벤트용)
- **도구/문서**: `lombok`, `springdoc-openapi-starter-webmvc-ui:2.8.14`
- **테스트**: `spring-boot-starter-test`, `spring-security-test`, `junit-platform-launcher`

### 2. DDD 패턴 기반의 폴더 구조 강제
모든 비즈니스 도메인은 `domain/` 하위에 위치하며, 다음과 같은 DDD 계층 구조를 엄격히 따릅니다.
```text
domain/{domain_name}/
├── presentation/   # Controller, DTO (외부 노출 계층)
├── application/    # Service, Facade (유스케이스 흐름 제어, 비즈니스 로직 작성 금지)
├── domain/         # Entity, Value Object, Repository 인터페이스 (핵심 비즈니스 규칙 및 검증)
└── infrastructure/ # Repository 구현체, Kafka Producer/Consumer, 외부 연동 (기술 구현체)
```
- **Controller 비즈니스 로직 작성 금지**: 핵심 비즈니스 규칙은 오직 Service/Domain에 구현합니다.
- **인프라 자동화**: 프로젝트 루트에 PostgreSQL, Redis, Kafka를 로컬에서 즉시 띄울 수 있는 `docker-compose.yml`을 포함하고, `application.yml` 파일로 제어합니다.

---

## 🎨 제3단계: 프론트엔드 초기 세팅 규칙 (`frontend/`)

### 1. 기술 스택 및 환경 제약
- **인증(로그인)**: 구현 범위에서 전면 제외합니다. 모든 페이지는 로그인 없이 접근 가능합니다.
- **런타임 및 빌드**: Vite + Yarn (npm 사용 절대 금지, ESLint + Prettier 적용)
- **프레임워크 및 라우팅**: React 18+, TypeScript, React Router v6+
- **스타일링 및 알림**: Tailwind CSS, Sonner (Three.js 및 Axios는 추후 추가하므로 설치 금지)

### 2. 엄격한 파일 분리 및 폴더 격리 규칙 (Strict Code Separation)
AI는 코드를 작성할 때 생산 편의성을 이유로 단일 파일에 여러 역할의 코드를 몰아넣지 마십시오.
- **TypeScript 타입/인터페이스**: `pages/`나 `components/` 내부에 절대 선언하지 마십시오. 오직 `src/types/` 폴더 하위에 도메인별 파일(예: `inbound.ts`)로 분리하여 추출해야 합니다.
- **가짜 데이터 및 상수**: 테스트용 데이터나 정적 상수는 `src/constants/` 폴더로 격리하십시오.
- **API 호출 로직**: 컴포넌트 내부에서 직접 Axios를 호출하지 말고, `src/services/` 하위에 도메인별 API 함수를 정의하여 호출하는 구조를 유지하십시오.
- **비즈니스 계산식/유틸**: 좌표 계산, 부피/중량 합산 등의 순수 함수 로직은 `src/utils/` 폴더로 분리하십시오.

### 3. 생성 코드 범위
- 설정 파일: `package.json`, `vite.config.ts`, `tsconfig.json`, `tailwind.config.js`
- 레이아웃: `layout/AppLayout.tsx` (물류 담당자용 Header, Sidebar 포함)
- 라우팅: `App.tsx` 또는 `router.tsx` (입고, 출고, 배차, 배송 관제 페이지 뼈대 연결)

---

## 📎 관련 문서

- `ai/logistics-ops-ai-harness/AGENTS.md`: 작업 루프·승인 게이트·완료 보고 형식을 정의한 에이전트 운영 계약
- `ai/logistics-ops-ai-harness/README.md`: 문서 읽기 순서와 7일 개발 계획
- `.ai/`: 설계 문서 11종
- `docks/development/DAY_01~07`: 일차별 개발 지시서
