# DOMAIN

## Aggregate와 책임

| Aggregate | 책임 | 주요 불변식 |
|---|---|---|
| Inbound | 입고 예정·검수·완료 | 완료 후 검수값 변경 금지 |
| OutboundPlan | 출고 대상과 배송지 묶음 | 완료 입고 물량만 포함, 수량 초과 금지 |
| Vehicle | 차량 적재능력·가용 상태 | 최대 중량/부피는 양수 |
| Driver | 기사 가용 상태 | 동일 시간대 활성 배차 1개 |
| Dispatch | 물량·차량·기사·경로 연결 | 과적·중복 할당 금지 |
| Route | 방문 순서와 거리 | 모든 배송지 1회 포함 |
| Anomaly | 운영 이상과 처리 상태 | 동일 원인 중복 알림 억제 |

## 상태 머신

### InboundStatus

`EXPECTED → INSPECTING → COMPLETED`

취소가 필요하면 `EXPECTED` 또는 `INSPECTING`에서만 `CANCELLED`로 이동할 수 있다.

### OutboundStatus

`DRAFT → READY → ASSIGNED → LOADED → RELEASED`

### DispatchStatus

`DRAFT → CONFIRMED → LOADED → IN_TRANSIT → COMPLETED`

- `DRAFT → CANCELLED`, `CONFIRMED → CANCELLED`만 허용한다.
- `COMPLETED`와 `CANCELLED`는 종결 상태다.
- 종결 상태에서 다른 상태로 이동할 수 없다.

### StopStatus

`PENDING → ARRIVED → DELIVERED`

배송 실패는 `ARRIVED → FAILED`로 기록하며 운영자가 재시도 정책을 결정한다. MVP에서는 자동 재배차하지 않는다.

## 실제 구현 (입고/출고, 단순화 버전)

현재 코드의 상태 머신은 위 이론 모델보다 단순하다.

- `InboundStatus`: `REQUESTED(입고 요청) → IN_PROGRESS(입고 처리) → COMPLETED(검수 완료, inspectedQuantity 기록)`, `CANCELLED`는 COMPLETED 전 언제든 가능.
- `OutboundStatus`: `REQUESTED(출고 계획) → PICKING(피킹) → SHIPPED(출고 완료)`, `CANCELLED`는 SHIPPED 전 언제든 가능.
- `Outbound`는 하나의 출고 계획이 여러 `Inbound`의 물량을 합쳐 구성할 수 있다(1:N). `OutboundItem{outboundId, inboundId, quantity}`로 표현하며, `inboundId`는 다른 도메인의 ID 참조일 뿐 FK나 JPA 연관관계를 걸지 않는다.
- 출고 생성 시 각 item마다 `Inbound.status == COMPLETED`이고 `inspectedQuantity - 이미 배정된 수량 >= 요청 수량`인지 검증한다. `OutboundService`는 `InboundRepository`를 직접 주입받지 않고 `InboundService`(Application Service)를 통해서만 Inbound 정보를 조회한다.

## 배차 가능성 규칙

차량 `v`와 출고 계획 집합 `O`에 대해 다음을 모두 만족해야 한다.

```text
sum(O.weightKg) <= v.maxWeightKg
sum(O.volumeM3) <= v.maxVolumeM3
v.status == AVAILABLE
driver.status == AVAILABLE
no schedule overlap
no outbound plan assigned to another active dispatch
```

후보 정렬 점수는 MVP에서 다음처럼 단순화한다.

```text
unusedWeightRatio + unusedVolumeRatio + distanceFromHubPenalty
```

점수가 낮을수록 적합하다. 자동 확정하지 않고 담당자가 선택한다.

## 경로 규칙

1. 허브를 시작점으로 Nearest Neighbor 초기 경로를 만든다.
2. 2-opt 교환을 반복해 총 거리가 더 짧아질 때만 반영한다.
3. 동일 입력과 동일 tie-breaker에는 동일 결과를 반환한다.
4. `optimizedDistance <= initialDistance`를 보장한다.

## 참고: Delivery 도메인과의 관계

실제 백엔드 코드(`backend/src/main/java/com/logistics/domain/delivery`)는 배송/관제(SSE, Kafka 소비) 책임을 `Dispatch`/`Route`와 별도의 `Delivery` 도메인 패키지로 분리해 구현되어 있다. 이 문서의 Aggregate 표는 `Dispatch`/`Route` 중심으로 기술되어 있어 실제 코드 구조와 이름이 완전히 일치하지 않는다. `Delivery`를 별도 Aggregate로 문서에도 명시할지, 배송 이력(`DeliveryHistory`)을 `dispatch_history`와 같은 것으로 볼지는 아직 결정되지 않았다 (`DECISION_LOG.md`의 미결정 항목 참고).

## 이상 유형

- `OVER_CAPACITY`: 중량 또는 부피 초과 시도
- `INVALID_TRANSITION`: 허용되지 않은 상태 전이
- `DRIVER_SCHEDULE_CONFLICT`: 기사 일정 중복
- `STALLED_DISPATCH`: IN_TRANSIT 상태가 기준 시간을 초과
- `DUPLICATE_ASSIGNMENT`: 물량의 활성 배차 중복

거부형 이상은 명령을 실패시키면서 감사용 기록을 남긴다. 지연형 이상은 배치 검사 또는 스케줄러가 생성한다.

