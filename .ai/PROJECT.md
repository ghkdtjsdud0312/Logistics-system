# PROJECT

## 프로젝트명

물류 입·출고 및 배차 최적화 시스템  
영문: Logistics Inbound/Outbound & Dispatch Optimization System

## 목적

물류 담당자가 입고 물량을 검수하고 출고 계획을 수립한 뒤, 물량 제약에 맞는 차량·기사를 배차하고 배송지 방문 순서를 최적화하며 배송 진행 상황을 관제하는 업무를 구현한다.

## 대상 사용자

- 물류 운영 담당자: 입고, 출고 계획, 배차 승인
- 배차 담당자: 차량/기사 선택, 적재 제약 확인
- 관제 담당자: 배송 진행 및 이상 상황 확인

## 핵심 개발 기조

- 취업 포트폴리오 목적의 개인 프로젝트로서, 핵심 비즈니스 로직(배차 검증 및 경로 최적화 알고리즘)의 무결성과 인프라 활용 능력 입증에 집중한다.
- 초기 단계에서는 복잡성을 줄이기 위해 회원가입 및 로그인(인증/인가) 기능 없이 모든 기능을 프리패스로 개발한다.

## 핵심 포트폴리오 증거

- 상태 기반 물류 도메인 모델링
- 차량 용량 제약을 포함한 배차 의사결정
- Nearest Neighbor와 2-opt 경로 개선
- Kafka 기반 후속 처리 분리
- Redis 캐시의 정량적 성능 비교
- SSE 실시간 이벤트 스트림
- 자동 테스트, 빌드, 문서 추적이 포함된 AI 개발 하네스

## 기술 스택

- Backend: Java 17, Spring Boot 3.x, Spring Data JPA, Bean Validation
- Database: PostgreSQL, Flyway
- Cache: Redis
- Event: Apache Kafka
- Realtime: SSE
- Frontend: React, TypeScript, Vite
- Test: JUnit 5, Testcontainers, REST Assured 또는 MockMvc, Vitest
- Performance: k6
- Runtime: Docker Compose
- API Docs: OpenAPI/Swagger

## 성공 정의

핵심 시나리오가 로컬에서 재현되고, 테스트와 측정 결과로 동작을 증명하며, README만으로 실행·시연·설계 의도를 이해할 수 있어야 한다.

