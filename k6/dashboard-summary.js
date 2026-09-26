// 대시보드 요약 조회(GET /api/dashboard/summary) 부하 테스트
// 사용: k6 run -e BASE_URL=http://localhost:8081 -e LABEL=cache-on k6/dashboard-summary.js
import http from 'k6/http';
import { check } from 'k6';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8081';
const LABEL = __ENV.LABEL || 'run';
const STEP_SEC = 30;
const STEP = `${STEP_SEC}s`;

// 워밍업(5 VU 10초) 후 20 → 100 → 300 VU를 각 30초씩 순서대로 실행한다.
const step = (vus, startTime) => ({
  executor: 'constant-vus',
  vus,
  duration: STEP,
  startTime,
  gracefulStop: '5s',
  exec: 'summary',
});

export const options = {
  scenarios: {
    warmup: { ...step(5, '0s'), duration: '10s' },
    vu20: step(20, '15s'),
    vu100: step(100, '50s'),
    vu300: step(300, '85s'),
  },
  // 단계별 지표를 요약에 노출하기 위한 항상 통과하는 임계값
  thresholds: Object.fromEntries(
    ['vu20', 'vu100', 'vu300'].flatMap((s) => [
      [`http_req_duration{scenario:${s}}`, ['max>=0']],
      [`http_req_failed{scenario:${s}}`, ['rate>=0']],
      [`http_reqs{scenario:${s}}`, ['count>=0']],
    ]),
  ),
};

export function summary() {
  const res = http.get(`${BASE_URL}/api/dashboard/summary`);
  check(res, { 'status 200': (r) => r.status === 200 });
}

export function handleSummary(data) {
  const rows = ['vu20', 'vu100', 'vu300'].map((s) => {
    const d = data.metrics[`http_req_duration{scenario:${s}}`]?.values ?? {};
    const r = data.metrics[`http_reqs{scenario:${s}}`]?.values ?? {};
    const f = data.metrics[`http_req_failed{scenario:${s}}`]?.values ?? {};
    return {
      scenario: s,
      requests: r.count,
      // r.rate는 전체 테스트 시간 기준이라 단계 시간으로 직접 나눈다.
      reqPerSec: Number(((r.count ?? 0) / STEP_SEC).toFixed(1)),
      avgMs: Number((d.avg ?? 0).toFixed(2)),
      p95Ms: Number((d['p(95)'] ?? 0).toFixed(2)),
      maxMs: Number((d.max ?? 0).toFixed(2)),
      errorRate: Number(((f.rate ?? 0) * 100).toFixed(2)),
    };
  });
  console.log(`\n[${LABEL}]\n` + rows.map((x) => JSON.stringify(x)).join('\n'));
  return {
    [`k6/results/${LABEL}.json`]: JSON.stringify({ label: LABEL, rows }, null, 2),
    [`k6/results/${LABEL}.raw.json`]: JSON.stringify(data, null, 2),
  };
}
