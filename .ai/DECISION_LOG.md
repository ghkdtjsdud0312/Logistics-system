# DECISION LOG

## ADR-001 모듈형 모놀리스 — 유지

- 결정: 단일 Spring Boot 애플리케이션 안에서 기능 패키지로 경계를 나눈다.
- 이유: 7일 동안 물류 규칙과 검증을 완성하고 분산 시스템 복잡도를 제한한다.
- 결과: MSA는 범위 밖이며 서비스 분리는 확장 과제로만 기록한다.

## ADR-002 담당자 승인형 배차 — Superseded (ADR-012: 후보 추천 제외)

- 결정: 시스템은 후보와 근거를 추천하고 담당자가 최종 확정한다.
- 이유: 고급 최적화 제약이 없는 MVP가 자동 의사결정을 과장하지 않도록 한다.

## ADR-003 좌표 기반 경로 근사 — Superseded (ADR-012: 경로 최적화 제외)

- 결정: Nearest Neighbor로 초기 경로를 만들고 2-opt로 개선한다.
- 이유: 외부 지도 API 없이 알고리즘과 테스트 가능성을 보여준다.
- 한계: 실제 도로, 교통, 시간창을 반영하지 않는다.

## ADR-004 Kafka의 제한적 사용 — 유지 (대상: 감사로그·대시보드 Consumer)

- 결정: 핵심 상태 변경은 동기 트랜잭션으로 확정하고, 이력 투영·이상 평가를 Consumer가 처리한다.
- 이유: 업무 성공 여부와 후속 처리를 분리하되 이벤트 기반 설계를 과도하게 확장하지 않는다.

## ADR-005 Redis의 검증 가능한 사용 — 수정 유지 (대상: 대시보드 요약, ADR-016)

- 결정: 배차 상세 조회에 cache-aside를 적용하고 상태 변경 후 무효화한다.
- 이유: 조회 집중 시나리오에서 k6로 효과와 한계를 측정할 수 있다.

## ADR-006 출고 품목 단위 적재량 입력 — Superseded (ADR-012: 상품 단위 중량으로 대체)

- 날짜: 2026-09-23
- 상태: Accepted
- 맥락: 배차 적재량 검증(TASK-005)을 구현하려면 중량/부피 데이터가 필요한데, `Inbound`/`Outbound` 어디에도 존재하지 않았다. 입고 시점 실측(옵션 B)이 실무에 더 가깝지만, Outbound가 `inboundId`만 참조하는 구조상 도메인 간 조회 인터페이스를 새로 설계해야 해 Day 3 범위를 넘어선다.
- 선택지: (A) `OutboundItem`에 `weightKg`/`volumeM3` 직접 입력, (B) `Inbound`에 실측값을 기록하고 Outbound가 참조.
- 결정: Day 3는 옵션 A로 진행한다. `OutboundItem`에 `weightKg`(양수), `volumeM3`(양수)를 추가하고 출고 등록 시 담당자가 입력한다.
- 근거: 도메인 1개만 변경하면 되어 Day 3 일정(차량 후보~배차 확정) 안에 완료 가능하고, 향후 옵션 B로 교체 가능한 구조로 남겨둔다.
- 결과와 위험: 같은 품목을 여러 번 출고할 때 값을 반복 입력해야 하며 담당자 입력 오차가 발생할 수 있다. 옵션 B 전환은 `TASKS.md`에 후속 과제로 기록한다.

## ADR-007 기사·차량 일정 겹침 판단 방식 — Superseded (ADR-012: 활성 배차 1건 규칙)

- 날짜: 2026-09-23
- 상태: Accepted
- 맥락: TASK-006 완료 조건("기사 일정 경계가 맞닿는 경우와 겹치는 경우" 테스트)은 시간 구간 겹침 판단을 요구하는데, `Dispatch`에는 계획 시각(`plannedAt`) 하나만 있고 종료 시각/소요시간 개념이 없었다. 이 값이 없으면 `Vehicle`/`Driver`의 상태(AVAILABLE 등)를 배차 시점에 ASSIGNED로 바꾸는 방식으로 단순화할 수도 있었지만, 그러면 하루 중 겹치지 않는 여러 배차를 같은 차량/기사에 할당할 수 없게 되어 DOMAIN.md의 "차량과 기사는 해당 시간에 가용해야 한다" + "일정 겹침 없음"이라는 두 개의 독립된 조건과 맞지 않는다.
- 선택지: (A) 배차 확정 시 차량/기사 status를 ASSIGNED로 전환해 동시에 최대 1건만 허용, (B) status는 정비/휴무 같은 거시 상태만 표현하고, 개별 배차는 계획 시각부터 고정 길이 창(예: 4시간)으로 겹침을 판단.
- 결정: 옵션 B. `DispatchWindow.of(plannedAt)`으로 `[plannedAt, plannedAt+4h)` 구간을 만들고, 같은 차량/기사의 활성 배차들과 겹치는지(`ScheduleConflictChecker`)로 판단한다. 경계가 맞닿는 경우(`end == start`)는 겹치지 않는 것으로 처리한다.
- 근거: 실제 하루 여러 건 배차를 표현할 수 있고, DOMAIN.md의 두 조건(가용 상태 + 일정 겹침)을 각각 독립적으로 구현할 수 있다.
- 결과와 위험: 배차 1건이 실제로 몇 시간 걸리는지는 아직 입력받지 않고 4시간 고정값을 가정한다. 실제 소요시간을 반영하려면 `plannedAt` 외에 예상 소요시간/종료시각 필드를 추가해야 한다 (후속 과제).

## ADR-008 배송지 좌표와 허브 위치 — Superseded (ADR-012: 좌표 제외)

- 날짜: 2026-09-23
- 상태: Accepted
- 맥락: Day 4 경로 최적화(TASK-007)는 "허브에서 시작해 배송지를 방문"하는 Nearest Neighbor + 2-opt를 요구하는데, `Outbound`에는 `destination`(문자열)만 있고 위경도가 없다. 허브(출발지) 좌표도 시스템 어디에도 정의돼 있지 않다.
- 선택지: (A) `Outbound`에 `latitude`/`longitude`를 출고 등록 시 직접 입력받고, 허브 좌표는 `application.yml`의 고정 설정값(`hub.latitude`/`hub.longitude`, env override 가능)으로 둔다. (B) 별도 `Warehouse`/`Address` 도메인과 지오코딩을 도입한다.
- 결정: 옵션 A. ADR-006과 같은 패턴(입력값을 실측/외부 연동 없이 담당자가 직접 입력)을 좌표에도 적용한다.
- 근거: 옵션 B는 Day 4 범위(실제 GPS/지오코딩은 범위 밖으로 명시됨)를 넘어서고, 새 도메인을 추가하면 Modular Monolith 경계가 늘어난다. 좌표는 위경도이므로 거리 계산은 평면 유클리드가 아닌 Haversine을 사용한다(DAY_04 문서 명시).
- 결과와 위험: 좌표를 잘못 입력하면 경로가 왜곡된다. 허브 좌표를 바꾸려면 배포 설정을 변경해야 한다 (다중 허브는 범위 밖).

## ADR-009 이상 탐지 기록 경로: 거부형은 동기, 지연형만 Kafka/스케줄러 — Superseded (ADR-012: 이상 탐지 제외)

- 날짜: 2026-09-23
- 상태: Accepted
- 맥락: Day 5(TASK-009)는 Kafka Consumer 2개(History, Anomaly)와 이상 탐지(OVER_CAPACITY 등)를 요구한다. DOMAIN.md는 이미 "거부형 이상은 명령을 실패시키면서 감사용 기록을 남긴다. 지연형 이상은 배치 검사 또는 스케줄러가 생성한다"고 정해두었다. 거부형(OVER_CAPACITY, DRIVER_SCHEDULE_CONFLICT, DUPLICATE_ASSIGNMENT, INVALID_TRANSITION)은 트랜잭션이 롤백되는 시점에 발생하므로, "커밋 후 발행" 원칙의 Kafka 이벤트로는 애초에 흘려보낼 수 없다(롤백되면 커밋 자체가 없다).
- 결정: 거부형 이상은 각 서비스(DispatchConfirmService/DispatchStatusService/RouteStopService)가 `BusinessException`을 잡아 `AnomalyService.record(...)`를 `REQUIRES_NEW` 트랜잭션으로 즉시 호출해 기록하고 원래 예외를 다시 던진다(Kafka 미경유). `STALLED_DISPATCH`는 `@Scheduled` 배치(`StalledDispatchDetector`, 테스트 가능하도록 `Clock` 빈 주입)가 생성한다. Kafka의 두 Consumer는 배차 상태가 성공적으로 바뀐(커밋된) 사건에만 반응한다: **HistoryConsumer**는 `dispatch_status_projection`(조회용 투영)을 멱등 갱신하고, **AnomalyConsumer**는 같은 이벤트를 소비하며 `processed_event`로 중복 처리를 막고 `aggregateVersion`이 이미 처리한 것보다 낮으면(역순 도착) 건너뛴다.
- 근거: 거부형 이상은 "명령이 실패했다"는 사실 자체가 즉시·확실하게 감사 기록으로 남아야 하므로 비동기 유실 위험이 있는 경로에 맡기지 않는다. 반면 Kafka는 DAY_05가 실제로 요구하는 두 가지(후속 투영 갱신, 멱등/순서 처리)에만 쓰여 "기술을 장식처럼 추가"하지 않는다.
- 결과와 위험: `DISPATCH_VEHICLE_UNAVAILABLE`(차량 상태/일정 불가)은 DAY_05의 이상 유형 5개(OVER_CAPACITY, INVALID_TRANSITION, DRIVER_SCHEDULE_CONFLICT, DUPLICATE_ASSIGNMENT, STALLED_DISPATCH)에 정확히 대응되는 항목이 없어 이상 기록에서 제외했다 (필요하면 후속 과제로 유형 추가).

## ADR-010 Redis 캐시 오류 처리와 테스트 전략 — 유지

- 날짜: 2026-09-23
- 상태: Accepted
- 맥락: "Redis 장애 시 DB 조회로 기능 유지"가 요구되는데, Spring의 기본 `RedisCacheManager`/`@Cacheable`는 Redis 연결 실패 시 예외를 그대로 전파한다. 또한 이 프로젝트는 Redis 통합 테스트용 embedded-redis/Testcontainers 의존성이 없다(H2로 DB만 대체하는 기존 관행과 같은 수준의 경량 대체재가 Redis엔 없음).
- 결정: `CacheErrorHandler`를 커스텀 구현해 캐시 GET/PUT/EVICT 실패를 로그만 남기고 삼켜서(swallow) 항상 원래 메서드(DB 조회)가 실행되도록 한다. 테스트 프로파일(`application-test.yml`)은 `RedisCacheManager` 대신 Spring 내장 `ConcurrentMapCacheManager`를 사용해 새 의존성 없이 캐시 히트/미스/무효화 동작을 검증하고, "Redis 장애 시 폴백"은 `CacheErrorHandler`를 직접 호출하는 단위 테스트로 검증한다.
- 근거: 새 인프라 의존성(Testcontainers 등)을 추가하지 않고도 캐시 로직과 장애 격리 요구사항을 모두 테스트할 수 있다.
- 결과와 위험: 테스트가 실제 Redis 직렬화(`GenericJackson2JsonRedisSerializer`)를 거치지 않으므로, 실제 Redis 직렬화 관련 버그는 테스트로 못 잡는다 (수동 기동 후 curl 검증으로 보완).

## ADR-011 도메인 재편과 협력 방식
- 날짜: 2026-09-25
- 상태: Accepted
- 맥락: 주문 하나가 재고·창고 작업·상차·배차·배송·반품을 관통하므로 도메인 간 협력 규칙이 필요하다.
- 결정: `master / inbound / inventory / order / warehouse / loading / dispatch / delivery / returns / audit / dashboard` 도메인으로 재편한다. 명령성 협력은 타 도메인 Application Service 호출(같은 트랜잭션), 관찰성 협력(감사로그·대시보드)은 Kafka 이벤트로 한다. Repository 주입·Entity 참조·JPA 연관은 금지하고 ID 참조만 허용한다. 주문 상태의 소유자는 `order`이다.
- 근거: CLAUDE.md의 Modular Monolith 제약을 지키면서 재고 예약·차감 같은 원자성이 필요한 흐름을 단순하게 유지한다.

## ADR-012 방향 전환: 물류 통합 관리 시스템
- 날짜: 2026-09-25
- 상태: Accepted
- 맥락: 프로젝트가 "입·출고 및 배차 최적화"에서 주문~고객 인도~반품 전 과정 관리로 바뀌었다. 개발 기간은 7일에서 3일(금~일)로 줄었다.
- 결정: 경로 최적화, 차량 후보 추천, 이상 탐지(anomaly), 좌표를 범위에서 제외하고 감사로그로 대체한다. 스케줄·지도·3D는 "나중에 추가"로 둔다. 일정은 `DEVELOPMENT_PHASES.md`의 3일 계획을 따른다. 제외 코드는 삭제하지 않고 TASK-001에서 별도 승인 후 정리한다.
- 결과: ADR-002, 003, 006, 007, 008, 009 Superseded.

## ADR-013 수량 규칙: 부분 처리 없음
- 날짜: 2026-09-25 / 상태: Accepted
- 결정: 피킹은 피킹수량 = 요청수량일 때만 완료(초과 금지, 부족하면 미완료 유지), 인도는 인도수량 = 배송수량일 때만 완료(다르면 실패 처리). 분할 적치·부분 출고·재배송·주문 취소는 범위 밖이다. 주문 상세의 "출고수량"은 "상차수량"으로 통일한다.

## ADR-014 API Base path
- 날짜: 2026-09-25 / 상태: Accepted
- 결정: `/api`를 유지하고 `/api/v1`은 쓰지 않는다(기존 구현과 일치, 이전 "확인 필요" 해소).

## ADR-015 사용자 식별
- 날짜: 2026-09-25 / 상태: Accepted
- 결정: 인증이 없으므로 `X-Actor` 헤더로 사용자를 받아 감사로그에 기록하고 없으면 `SYSTEM`으로 기록한다. 권한 검사에는 쓰지 않는다.

## ADR-016 대시보드 지표 정의
- 날짜: 2026-09-25 / 상태: Accepted
- 결정: 카드 7개(주문, 피킹대기, 포장대기, 상차대기, 배송중, 배송완료, 배송실패)를 오늘 주문일 기준으로 집계하고, 진행 현황 막대는 단계별 현재 건수로 한다. 요약은 Redis에 캐시하고 상태 변경 이벤트로 무효화하며 SSE로 갱신한다. "로그인 후 첫 화면"은 "접속 시 첫 화면"으로 해석한다.

## ADR-017 상차 → 배차 순서와 Shipment
- 날짜: 2026-09-25 / 상태: Accepted (기획서와 다름)
- 맥락: 기획서의 상차 화면에는 차량·기사 선택이 있고 배차 화면에도 있어 중복된다. 기존 구현은 배차 후 상차(`CONFIRMED → LOADED`)였다.
- 결정: 상차(차량·기사 없이 주문만 묶음, 주문별 Shipment 생성) → 배차 등록(차량·기사·시각 배정, Shipment N건 묶음) → 배송 시작. 상차 화면의 차량·기사 선택은 제거한다. 배송현황·완료·실패는 Shipment 단위다. 적재량(kg) 검증은 배차 등록 시 수행한다.

## ADR-018 재고 예약·차감·복구 시점
- 날짜: 2026-09-25 / 상태: Accepted
- 결정: 예약은 주문 생성 시(재고 부족이면 주문 거절, 위치 코드 순 배분), 차감은 피킹 완료 시(현재·예약 동시 차감), 복구는 반품입고 시(사유가 상품 파손이 아니면 지정 위치에 복구)로 한다. 재고 행은 낙관적 잠금으로 보호한다.

## ADR-019 스키마 관리와 기존 코드 정리
- 날짜: 2026-09-25 / 상태: Accepted
- 결정: 3일 일정이라 Flyway는 도입하지 않고 로컬 개발은 `JPA_DDL_AUTO=update`(기본값)로 엔티티에서 테이블을 생성한다. 옛 테이블(`outbound`, `anomaly`, `route_stop` 등)은 로컬 DB에 남을 수 있으며 필요하면 DB를 초기화한다. `outbound`, `anomaly`, `dispatch`, `delivery`, `inbound` 도메인은 삭제 후 새 스펙대로 재작성하고, `vehicle`/`driver`는 컬럼(`capacityKg`, `driverCode`, `phone`)과 상태(`IN_OPERATION`, `DELIVERING`)를 조정해 유지한다. Kafka/SSE/Cache 설정은 `global/`에서 재사용하고 상태 변경 이벤트 공통 발행은 `global/event`에 둔다.
- 위험: `update`는 컬럼 삭제·타입 변경을 반영하지 않는다. 운영 환경에는 부적합하며 README에 한계로 기록한다.

## ADR-020 대시보드 캐시 컴포넌트와 진행 현황 키
- 날짜: 2026-09-26 / 상태: Accepted
- 결정: 대시보드 요약은 Spring Cache 대신 `DashboardCache`(Redis JSON, 키 `dashboard:summary`, TTL 30초)로 캐시한다. Redis 장애 시 예외를 삼키고 DB로 폴백한다(ADR-010의 정신 유지). 테스트 프로파일은 인메모리 구현을 쓴다. `DashboardConsumer`가 상태 변경 이벤트로 캐시를 비우고 SSE(`status-changed`)를 보낸다. 진행 현황 키는 `ORDERS`, `PICKING`, `PACKING`, `LOADING`, `DELIVERY`로 확정한다.
- 근거: 기록(record) DTO를 Redis 기본 직렬화(default typing)로 되살리기 어렵고, 캐시 동작을 명시적으로 검증할 수 있다.
- 한계: 감사로그 Consumer와 대시보드 Consumer는 서로 다른 그룹이라 "최근 이벤트" 조회가 방금 발생한 이벤트보다 잠깐 늦을 수 있다. 화면은 SSE로 받은 이벤트를 목록 맨 앞에 바로 추가해 보완한다.

## ADR-021 3D 창고 (Three.js)
- 날짜: 2026-09-26 / 상태: Accepted
- 결정: 사용자 요청으로 `three`와 `@types/three`를 추가하고(CLAUDE.md의 "추후 추가" 항목), 창고·위치관리 화면의 "트리 / 3D 보기" 탭으로 3D 창고를 구현한다. 창고 트리 API와 재고 API만 사용하며 백엔드 변경은 없다. 위치 코드 `A-01-02`를 (열 1, 단 2)로 해석해 배치하고 해석할 수 없거나 겹치는 코드는 6열 격자로 배치한다. 칸 색은 재고 상태(없음, 가용, 예약 있음, 소진)로 정하고 SSE 이벤트로 재조회한다. three는 3D 탭을 열 때만 지연 로딩한다.
- 한계: 실제 크기·좌표 데이터가 없어 배치는 코드 규칙에 의존한다. 이 개발 환경에서는 브라우저를 실행할 수 없어 렌더링을 직접 확인하지 못했다(배치·재고 상태 로직만 값으로 검증).

## 미결정 항목 (확인 필요)

아래는 방향 전환 문서화 과정에서 사용자 확인 없이 초안으로 정한 부분이다. 확인 후 ADR로 승격하거나 수정한다.

1. **출고 지시 단계**: 화면 기획에 없는 "출고 지시" 동작(주문접수 → 출고대기, 피킹 작업 생성)을 추가했다. 주문 생성과 동시에 자동 지시할지 확인 필요.
2. **입고완료와 적치대기의 분리**: 기획의 4개 입고 상태를 유지하되 `receive` 한 번에 입고완료 → 적치대기로 연속 전이(감사로그에는 두 건)하도록 초안을 잡았다.
3. **배차 취소**: 기획에 없는 `배송 시작 전 취소` 동작을 초안에 넣었다. 필요 없으면 제거한다.
4. **재고상태 검색 기준**: `AVAILABLE`(가용>0) / `SOLD_OUT`(가용=0) 두 값으로 초안을 잡았다.
5. ~~스키마 관리 방식~~ → ADR-019로 결정(2026-09-25).
6. ~~기존 코드 정리 범위~~ → ADR-019로 결정(2026-09-25).
7. **SSE 재연결·Kafka 발행 실패**: `EventSource` 기본 재연결과 "발행 실패 시 재처리는 README에 한계로 기록" 수준까지만 정했다.
8. ~~대시보드 최근 이벤트의 원천~~ → `audit_log` 최신순으로 확정(ADR-020).

## 새 결정 기록 형식

```markdown
## ADR-XXX 제목
- 날짜:
- 상태: Proposed / Accepted / Superseded
- 맥락:
- 선택지:
- 결정:
- 근거:
- 결과와 위험:
```

