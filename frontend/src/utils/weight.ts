import { Shipment } from '@/types/shipment';

/** 선택한 Shipment들의 총 중량(kg) */
export function sumWeight(shipments: Shipment[], selectedIds: number[]): number {
  return shipments
    .filter((s) => selectedIds.includes(s.id))
    .reduce((sum, s) => sum + s.totalWeightKg, 0);
}

/** 적재율(%). 적재량이 0이면 0 */
export function loadPercent(weightKg: number, capacityKg: number): number {
  return capacityKg === 0 ? 0 : Math.round((weightKg / capacityKg) * 100);
}
