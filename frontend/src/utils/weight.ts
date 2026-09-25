import { Shipment } from '@/types/shipment';

/** 선택한 Shipment들의 총 중량(kg) */
export function sumWeight(shipments: Shipment[], selectedIds: number[]): number {
  return shipments
    .filter((s) => selectedIds.includes(s.id))
    .reduce((sum, s) => sum + s.totalWeightKg, 0);
}
