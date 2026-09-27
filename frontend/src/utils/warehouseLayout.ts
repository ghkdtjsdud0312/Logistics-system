import { WarehouseNode, ZoneNode } from '@/types/warehouse';
import { SceneBounds, WarehouseLayout } from '@/types/warehouse3d';

/** 구역 바닥판을 가리키는 hover/select 코드(위치 코드와 겹치지 않는다) */
export function zoneHoverCode(zoneId: number): string {
  return `zone:${zoneId}`;
}

/** 방(창고) 바닥·배지를 가리키는 클릭 코드(위치·구역 코드와 겹치지 않는다) */
export function roomHoverCode(warehouseId: number): string {
  return `room:${warehouseId}`;
}

const CELL = 1.6;
const LEVEL = 1.3;
const ZONE_GAP = 4;
const WAREHOUSE_GAP = 6;
const FALLBACK_COLS = 6;

interface Placed {
  id: number;
  code: string;
  col: number;
  level: number;
}

/** 'A-01-02' → 열 0, 단 1. 숫자 두 개 이상이 없으면 해석할 수 없다. */
function parseSlot(code: string): { col: number; level: number } | null {
  const nums = code
    .split('-')
    .filter((p) => /^\d+$/.test(p))
    .map(Number);
  if (nums.length < 2) return null;
  return { col: nums[nums.length - 2] - 1, level: nums[nums.length - 1] - 1 };
}

/** 코드를 모두 해석할 수 있고 겹치지 않으면 코드대로, 아니면 6열 격자로 배치한다. */
function placeZone(zone: ZoneNode): Placed[] {
  const sorted = [...zone.locations].sort((a, b) => a.code.localeCompare(b.code));
  const slots = sorted.map((l) => parseSlot(l.code));
  const valid = slots.every((s) => s !== null && s.col >= 0 && s.level >= 0);
  const unique = new Set(slots.map((s) => `${s?.col}:${s?.level}`)).size === slots.length;
  return sorted.map((l, i) => {
    const slot = valid && unique ? slots[i] : null;
    return {
      id: l.id,
      code: l.code,
      col: slot ? slot.col : i % FALLBACK_COLS,
      level: slot ? slot.level : Math.floor(i / FALLBACK_COLS),
    };
  });
}

/** 창고는 x축으로, 구역은 z축으로, 위치는 열(x)과 단(y)으로 배치한다. */
export function layoutWarehouses(tree: WarehouseNode[]): WarehouseLayout {
  const layout: WarehouseLayout = { boxes: [], slabs: [] };
  let offsetX = 0;
  tree.forEach((warehouse) => {
    let maxCols = 1;
    warehouse.zones.forEach((zone, zoneIndex) => {
      const placed = placeZone(zone);
      const cols = Math.max(1, ...placed.map((p) => p.col + 1));
      maxCols = Math.max(maxCols, cols);
      placed.forEach((p) =>
        layout.boxes.push({
          locationId: p.id,
          code: p.code,
          zoneCode: zone.code,
          warehouseName: warehouse.name,
          x: offsetX + p.col * CELL,
          y: p.level * LEVEL + LEVEL / 2,
          z: zoneIndex * ZONE_GAP,
        }),
      );
      layout.slabs.push({
        zoneId: zone.id,
        label: zone.name,
        x: offsetX + ((cols - 1) * CELL) / 2,
        z: zoneIndex * ZONE_GAP,
        width: cols * CELL + 0.6,
        depth: 2.6,
      });
    });
    offsetX += maxCols * CELL + WAREHOUSE_GAP;
  });
  return layout;
}

/**
 * 카메라를 맞추기 위한 장면의 중심과 크기. 내용이 작아도 화면을 채우도록 여백만 살짝 둔다.
 * 구역이 없는 창고(빈 배치)도 다룰 수 있도록 안전한 기본값을 둔다.
 */
export function sceneBounds(layout: WarehouseLayout): SceneBounds {
  const xs = layout.slabs.flatMap((s) => [s.x - s.width / 2, s.x + s.width / 2]);
  const zs = layout.slabs.flatMap((s) => [s.z - s.depth / 2, s.z + s.depth / 2]);
  const maxY = Math.max(1, ...layout.boxes.map((b) => b.y + LEVEL / 2));
  const [minX, maxX] = [Math.min(...xs, 0), Math.max(...xs, 1)];
  const [minZ, maxZ] = [Math.min(...zs, 0), Math.max(...zs, 1)];
  const content = Math.max(maxX - minX, maxZ - minZ, maxY);
  return {
    center: { x: (minX + maxX) / 2, y: maxY / 2, z: (minZ + maxZ) / 2 },
    size: content * 1.3 + 1.5,
  };
}
