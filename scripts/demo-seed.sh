#!/usr/bin/env bash
# 데모 기준 데이터를 만든다: 상품 3종, 창고·구역·위치, 차량 2대, 기사 2명, 각 상품 입고 100개 적치.
# 빈 DB에서 백엔드를 띄운 직후 한 번만 실행한다.  사용법: ./scripts/demo-seed.sh [API 주소]
set -euo pipefail

API="${1:-http://localhost:8080/api}"
ACTOR="$(python3 -c 'import urllib.parse; print(urllib.parse.quote("관리자"))')"

call() { # 메서드 경로 [본문] → 응답의 data.id (또는 data)
  curl -sf -H 'Content-Type: application/json' -H "X-Actor: ${ACTOR}" -X "$1" "${API}$2" ${3:+-d "$3"} \
    | python3 -c 'import sys, json; d = json.load(sys.stdin)["data"]; print(d["id"] if isinstance(d, dict) else d)'
}

echo "1) 창고·구역·위치"
WAREHOUSE=$(call POST /warehouses '{"code":"A","name":"A창고"}')
ZONE=$(call POST "/warehouses/${WAREHOUSE}/zones" '{"code":"A01","name":"A구역"}')
LOC1=$(call POST "/zones/${ZONE}/locations" '{"code":"A-01-01"}')
LOC2=$(call POST "/zones/${ZONE}/locations" '{"code":"A-01-02"}')
LOC3=$(call POST "/zones/${ZONE}/locations" '{"code":"A-01-03"}')

echo "2) 상품, 차량, 기사"
WATER=$(call POST /products '{"code":"WATER001","name":"생수 500ml","unit":"EA","unitWeightKg":0.5}')
RAMEN=$(call POST /products '{"code":"RAMEN001","name":"라면","unit":"EA","unitWeightKg":0.12}')
DRINK=$(call POST /products '{"code":"DRINK001","name":"음료","unit":"EA","unitWeightKg":0.35}')
call POST /vehicles '{"vehicleNumber":"12가1234","vehicleType":"1톤","capacityKg":1000}' >/dev/null
call POST /vehicles '{"vehicleNumber":"34나5678","vehicleType":"2.5톤","capacityKg":2500}' >/dev/null
call POST /drivers '{"driverCode":"D001","name":"홍길동","phone":"010-0000-0001"}' >/dev/null
call POST /drivers '{"driverCode":"D002","name":"김영수","phone":"010-0000-0002"}' >/dev/null

echo "3) 입고 100개씩 → 입고완료 → 적치"
for pair in "${WATER}:${LOC1}" "${RAMEN}:${LOC2}" "${DRINK}:${LOC3}"; do
  PRODUCT="${pair%%:*}"; LOCATION="${pair##*:}"
  INBOUND=$(call POST /inbounds "{\"partnerName\":\"거래처\",\"productId\":${PRODUCT},\"quantity\":100,\"inboundDate\":\"$(date +%F)\"}")
  call PATCH "/inbounds/${INBOUND}/receive" >/dev/null
  call PATCH "/inbounds/${INBOUND}/putaway" "{\"locationId\":${LOCATION}}" >/dev/null
done

echo "완료: 재고 현황 화면에서 생수·라면·음료 각 100개(가용 100)를 확인하세요."
