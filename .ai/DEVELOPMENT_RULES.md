# DEVELOPMENT RULES

## 코드 작성 금지 원칙

- Controller에 절대 비즈니스 로직을 작성하지 않는다. 핵심 비즈니스 규칙은 오직 Service 또는 Domain(Entity/VO)에 구현한다.
- 요구사항에 없는 기능(예: 통계 대시보드 등)을 임의로 추가하지 않는다.

## 코드 품질

- 생성자 주입을 사용한다.
- DTO, Entity, Domain 객체의 책임을 분리한다.
- 도메인 상태 변경은 의미 있는 메서드로만 수행한다.
- 예외는 오류 코드가 있는 프로젝트 예외로 변환한다.
- `Clock`, ID 생성기, 거리 계산기를 주입 가능하게 만든다.
- `Optional.get()`, 광범위한 `catch (Exception)`, 무의미한 null 반환을 금지한다.
- N+1, 무제한 목록 조회, Controller의 Repository 직접 호출을 금지한다.

## 변경 규칙

1. 코드보다 관련 문서와 테스트 기대값을 먼저 확인한다.
2. 최소 변경으로 한 Task만 완료한다.
3. 새 의존성은 승인 없이 추가하지 않는다.
4. API 또는 DB 계약 변경은 문서를 먼저 갱신하고 승인받는다.
5. 기존 테스트 삭제·비활성화·완화로 통과시키지 않는다.
6. 비밀값은 환경변수로 주입하고 저장소에 커밋하지 않는다.

## 코드 품질 및 분리 컨벤션

- **Backend**: Java 가독성 유지, Lombok 활용, 글로벌 예외 처리 필수
- **Frontend**: ESLint + Prettier 준수, Tailwind CSS 활용
- **Strict Separation**: TypeScript 타입은 무조건 `src/types/` 하위로 가며 페이지 파일 내 선언을 금지한다. 가짜 데이터 및 상수는 `src/constants/`, API 호출은 `src/services/`, 순수 유틸리티는 `src/utils/`로 격리한다.
- **코드 크기 제한**: 모든 소스 파일은 300~400자(30~50줄) 이하로 제한하며, 초과가 불가피할 시 사전에 분리 계획을 사용자에게 보고하여 승인받는다.

## Git 규칙

- 한 커밋은 한 Task 또는 하나의 응집된 변경을 담는다.
- 권장 메시지: `feat(dispatch): TASK-005 차량 후보 조회 구현`
- 자동 생성 결과물과 로컬 비밀파일은 제외한다.
- 커밋 전 `git diff`로 의도하지 않은 변경을 확인한다.

## AI 작업 요청 템플릿

```markdown
# Task Request
- Task: TASK-XXX
- Goal:
- In scope:
- Out of scope:
- Related requirements:
- Acceptance criteria:
- Required validation:

먼저 저장소와 관련 문서를 조사하고 영향 범위와 계획을 제시하라.
승인 게이트에 해당하면 중단하고 질문하라. 아니면 구현부터 검증·문서화까지 계속하라.
```

## AI 자체 검토 질문

- 요구사항 ID를 실제 코드와 테스트가 만족하는가?
- 상태 변경 경로를 우회할 수 있는가?
- 동시 요청 또는 재시도에 중복 데이터가 생기는가?
- 단위와 시간대가 명확한가?
- 캐시와 DB가 불일치할 수 있는 변경 경로가 있는가?
- Consumer 재처리가 멱등한가?
- 실패 케이스가 정상 케이스만큼 테스트되었는가?

