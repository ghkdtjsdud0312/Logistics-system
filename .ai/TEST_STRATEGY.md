# TEST STRATEGY

## 테스트 피라미드

- Domain unit: 상태 머신, 용량 검증, 후보 점수, 거리/2-opt
- Application unit: 유스케이스 orchestration, 예외, 캐시 무효화
- Repository integration: PostgreSQL + Testcontainers
- Messaging integration: Kafka Consumer 멱등성
- API integration: 요청 검증, 상태 코드, 오류 계약
- E2E smoke: 입고부터 배송 완료까지 대표 시나리오
- Performance: k6 조회 부하 비교

## 필수 케이스

### 입고/출고

- 완료되지 않은 입고 물량의 출고 차단
- 검수 수량을 넘는 출고 차단
- 동일 Idempotency-Key 중복 생성 방지

### 배차

- 중량/부피 경계값과 초과값
- 운행 불가 차량 제외
- 기사 일정 중복 차단
- 동일 출고 계획의 이중 배차 차단
- 동시 배차 요청 중 하나만 성공

### 경로

- 배송지 0개, 1개, 여러 개
- 모든 배송지 정확히 한 번 포함
- 동일 입력의 결정적 결과
- 최적화 후 거리가 증가하지 않음
- 알려진 좌표 세트의 기대 거리

### 상태/이벤트

- 모든 허용 전이
- 모든 주요 금지 전이
- 종결 상태 변경 차단
- 이벤트 중복 소비 시 이력/이상 중복 없음
- 상태 변경 후 캐시 무효화

## k6 기준

고정 데이터와 같은 실행 환경에서 캐시 OFF/ON을 각각 워밍업 후 측정한다.

- 시나리오: `GET /api/v1/dispatches/{id}`
- 단계: 20 → 100 → 300 VU
- 기록: 평균, p95, req/s, 오류율, DB query count
- 기본 임계값: 오류율 < 1%, p95 < 500ms(로컬 참고 기준)

성능 향상을 미리 단정하지 않고 원시 결과와 환경을 함께 기록한다.

## Definition of Done

- [ ] 연결된 REQ/Acceptance Criteria 충족
- [ ] 컴파일·포맷·정적 검사 성공
- [ ] 정상/경계/예외 테스트 작성
- [ ] 관련 테스트와 전체 테스트 성공
- [ ] Docker 또는 로컬 실행 확인
- [ ] API/DB/도메인 문서 동기화
- [ ] diff에 범위 밖 변경 없음
- [ ] 미해결 위험과 미실행 검증 보고

