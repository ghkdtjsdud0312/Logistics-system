import { SceneBounds, WorldLayout, WorldRoom } from '@/types/warehouse3d';
import { WarehouseNode } from '@/types/warehouse';
import { layoutWarehouses, sceneBounds } from '@/utils/warehouseLayout';

const ROOM_GAP = 2.5;
const MIN_ROOM_SIZE = 6;

/** 원점에서 사각 나선으로 뻗어 나가는 좌표. 창고가 늘 때마다 옆·위로 방이 하나씩 붙는다. */
function spiralCoords(count: number): { col: number; row: number }[] {
  const coords = [{ col: 0, row: 0 }];
  const dCol = [1, 0, -1, 0];
  const dRow = [0, 1, 0, -1];
  let col = 0;
  let row = 0;
  let dir = 0;
  let step = 1;
  while (coords.length < count) {
    for (let turn = 0; turn < 2 && coords.length < count; turn++) {
      for (let i = 0; i < step && coords.length < count; i++) {
        col += dCol[dir];
        row += dRow[dir];
        coords.push({ col, row });
      }
      dir = (dir + 1) % 4;
    }
    step++;
  }
  return coords;
}

/**
 * 창고마다 실제 구역·위치를 배치하고, 창고 하나당 방 하나로 나선형 격자에 놓는다.
 * 방 크기는 다 같지만(정돈된 격자), 방을 클릭했을 때 확대할 범위(closeup)는 그 창고의 실제 내용에 맞춘다.
 * 재고(색)에 따라 달라지는 부분은 여기 없다 — 재고가 바뀔 때마다 장면을 다시 만들지 않기 위해서다.
 */
export function layoutWorld(tree: WarehouseNode[]): WorldLayout {
  const perWarehouse = tree.map((warehouse) => {
    const local = layoutWarehouses([warehouse]);
    const closeup = sceneBounds(local);
    return { warehouse, local, closeup };
  });
  const roomSize = Math.max(MIN_ROOM_SIZE, ...perWarehouse.map((p) => p.closeup.size));
  const step = roomSize + ROOM_GAP;
  const coords = spiralCoords(Math.max(tree.length, 1));

  const layout: WorldLayout = { rooms: [], boxes: [], slabs: [] };
  perWarehouse.forEach(({ warehouse, local, closeup }, i) => {
    const roomX = coords[i].col * step;
    const roomZ = coords[i].row * step;
    const dx = roomX - closeup.center.x;
    const dz = roomZ - closeup.center.z;
    const room: WorldRoom = {
      warehouseId: warehouse.id,
      number: i + 1,
      name: warehouse.name,
      x: roomX,
      z: roomZ,
      size: roomSize,
      closeup: { center: { x: roomX, y: closeup.center.y, z: roomZ }, size: closeup.size },
    };
    layout.rooms.push(room);
    local.boxes.forEach((b) => layout.boxes.push({ ...b, x: b.x + dx, z: b.z + dz }));
    local.slabs.forEach((s) => layout.slabs.push({ ...s, x: s.x + dx, z: s.z + dz }));
  });
  return layout;
}

/** 캠퍼스 전체(모든 방)가 보이는 카메라 범위 */
export function worldBounds(layout: WorldLayout): SceneBounds {
  const xs = layout.rooms.flatMap((r) => [r.x - r.size / 2, r.x + r.size / 2]);
  const zs = layout.rooms.flatMap((r) => [r.z - r.size / 2, r.z + r.size / 2]);
  const [minX, maxX] = [Math.min(...xs, 0), Math.max(...xs, 1)];
  const [minZ, maxZ] = [Math.min(...zs, 0), Math.max(...zs, 1)];
  return {
    center: { x: (minX + maxX) / 2, y: 0, z: (minZ + maxZ) / 2 },
    size: Math.max(maxX - minX, maxZ - minZ, MIN_ROOM_SIZE) * 1.05,
  };
}
