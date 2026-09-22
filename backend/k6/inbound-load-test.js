import http from 'k6/http';
import { check, sleep } from 'k6';

// 사용법: k6 run k6/inbound-load-test.js
export const options = {
  vus: 20,
  duration: '30s',
};

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

export default function () {
  const payload = JSON.stringify({
    itemName: '파렛트 A',
    quantity: 10,
    warehouseLocation: 'A-01',
  });

  const res = http.post(`${BASE_URL}/api/inbounds`, payload, {
    headers: { 'Content-Type': 'application/json' },
  });

  check(res, {
    'status is 201': (r) => r.status === 201,
  });

  sleep(1);
}
