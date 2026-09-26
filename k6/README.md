# k6 부하 테스트: 대시보드 요약 캐시 OFF/ON 비교

대상 `GET /api/dashboard/summary`. Redis 캐시(Cache-Aside, TTL 30초)를 껐을 때와 켰을 때의 응답시간·처리량을 비교한다.

## 방법

- 워밍업(5 VU, 10초) 후 20 → 100 → 300 VU를 각 30초씩 실행한다. 요청 사이 대기(sleep)는 없다.
- 캐시 스위치는 `app.cache.dashboard-enabled`(`CACHE_DASHBOARD_ENABLED`, 기본 true). OFF는 조회마다 DB에서 상태별 집계를 한다.
- 개발 중인 백엔드(8080)와 분리해 측정용 백엔드를 8081에 따로 띄웠다: DB `logistics_k6`, Redis DB 1, Kafka 컨슈머 그룹 `k6-bench`, `JPA_SHOW_SQL=false`, 로그 WARN.
- 데이터: 오늘 주문 1,000건(10개 상태 각 100건, 품목 1개).

```bash
# 측정용 백엔드 (캐시 OFF는 CACHE_DASHBOARD_ENABLED=false 추가)
SERVER_PORT=8081 DB_NAME=logistics_k6 JPA_SHOW_SQL=false \
  java -jar backend/build/libs/*SNAPSHOT.jar --spring.data.redis.database=1 \
  --spring.kafka.consumer.group-id=k6-bench --logging.level.root=WARN
k6 run -e BASE_URL=http://localhost:8081 -e LABEL=cache-off k6/dashboard-summary.js
```

## 결과 (2026-09-26)

| VU | 캐시 | 평균(ms) | p95(ms) | 최대(ms) | 처리량(req/s) | 오류율 |
|---|---|---|---|---|---|---|
| 20 | OFF | 6.49 | 9.87 | 89.95 | 3,061 | 0% |
| 20 | ON | 1.65 | 2.73 | 24.96 | 11,686 | 0% |
| 100 | OFF | 31.58 | 45.38 | 174.68 | 3,162 | 0% |
| 100 | ON | 9.32 | 19.40 | 199.14 | 10,643 | 0% |
| 300 | OFF | 97.25 | 153.75 | 774.51 | 3,087 | 0% |
| 300 | ON | 30.00 | 91.19 | 730.17 | 9,965 | 0% |

원시 결과: `results/cache-off.raw.json`, `results/cache-on.raw.json` (요약은 `*.json`).

## 해석과 한계

- 캐시 ON이 평균 응답시간은 약 3.4~3.9배 짧고 처리량은 약 3.3~3.8배 높았다. OFF는 VU를 늘려도 처리량이 약 3,100 req/s에서 더 오르지 않고 지연만 늘었다.
- 최대 응답시간(max)은 300 VU에서 OFF/ON 차이가 작다. 캐시가 꼬리 지연을 없애 주지는 못했다.
- 한 대의 노트북(Apple M1, 8코어, 16GB)에서 앱·PostgreSQL·Redis·Kafka(Docker)·k6를 함께 돌렸다. k6 자체도 CPU를 쓰므로 절대 수치보다 OFF/ON의 상대 비교로 본다.
- 주문 1,000건 규모다. 데이터가 더 커지면 OFF의 집계 비용은 커지고 ON과의 차이도 커질 수 있으나 이번에 측정하지 않았다.
- 각 조건을 1회씩만 측정했다(반복·신뢰구간 없음). 오늘 주문 기준 단일 키 조회라서 다양한 조회 패턴은 반영하지 못한다.
