import { Option } from '@/types/ui';
import { WarehouseNode, ZoneNode } from '@/types/warehouse';

export function warehouseOptions(tree: WarehouseNode[]): Option[] {
  return tree.map((w) => ({ value: String(w.id), label: w.name }));
}

export function zonesOf(tree: WarehouseNode[], warehouseId?: string): ZoneNode[] {
  return tree.filter((w) => !warehouseId || String(w.id) === warehouseId).flatMap((w) => w.zones);
}

export function zoneOptions(tree: WarehouseNode[], warehouseId?: string): Option[] {
  return zonesOf(tree, warehouseId).map((z) => ({
    value: String(z.id),
    label: `${z.code} ${z.name}`,
  }));
}

/** 적치 위치 선택용: '창고 / 구역 / 위치코드' 라벨 */
export function locationOptions(tree: WarehouseNode[]): Option[] {
  return tree.flatMap((w) =>
    w.zones.flatMap((z) =>
      z.locations.map((l) => ({ value: String(l.id), label: `${w.name} / ${z.code} / ${l.code}` })),
    ),
  );
}
