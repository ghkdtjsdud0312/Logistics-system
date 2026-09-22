import { AvailableInbound } from '@/types/outbound';

export interface WarehouseSummary {
  warehouseLocation: string;
  totalQuantity: number;
  items: AvailableInbound[];
}

/** 가용 입고 목록을 창고 위치 기준으로 묶어 위치별 재고 요약을 만든다 */
export function groupByWarehouseLocation(available: AvailableInbound[]): WarehouseSummary[] {
  const map = new Map<string, WarehouseSummary>();

  for (const item of available) {
    const existing = map.get(item.warehouseLocation);
    if (existing) {
      existing.totalQuantity += item.availableQuantity;
      existing.items.push(item);
    } else {
      map.set(item.warehouseLocation, {
        warehouseLocation: item.warehouseLocation,
        totalQuantity: item.availableQuantity,
        items: [item],
      });
    }
  }

  return [...map.values()].sort((a, b) => a.warehouseLocation.localeCompare(b.warehouseLocation));
}
