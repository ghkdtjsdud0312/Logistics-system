import * as THREE from 'three';
import { CELL_STATE_COLOR } from '@/constants/cellState';
import { ActivePicking, CellState, StockCell } from '@/types/warehouse3d';
import { cellState, isRecentlyChanged } from '@/utils/stockCells';

export const SELECTED_COLOR = 0x1d4ed8;
export const PICKING_COLOR = 0xf59e0b;

export interface SceneMeshes {
  locationMeshes: Map<string, THREE.Mesh>;
  rings: Map<string, THREE.LineSegments>;
  roomMeshes: Map<string, THREE.Mesh>;
}

/** 재고·선택·피킹 상태에 맞춰 장면을 다시 만들지 않고 색만 다시 칠한다. */
export function recolorScene(
  meshes: SceneMeshes,
  cells: Record<string, StockCell>,
  activePicking: Record<string, ActivePicking>,
  selectedCode: string | null,
  roomStates: Record<number, CellState>,
) {
  meshes.locationMeshes.forEach((mesh, code) => {
    const material = mesh.material as THREE.MeshStandardMaterial;
    material.color.setHex(CELL_STATE_COLOR[cellState(cells[code])]);
    const picking = Boolean(activePicking[code]) && code !== selectedCode;
    material.emissive.setHex(
      code === selectedCode ? SELECTED_COLOR : picking ? PICKING_COLOR : 0x000000,
    );
    material.emissiveIntensity = 0.6;
  });
  meshes.rings.forEach((ring, code) => {
    ring.visible = isRecentlyChanged(cells[code]);
  });
  meshes.roomMeshes.forEach((mesh, code) => {
    const warehouseId = Number(code.slice('room:'.length));
    (mesh.material as THREE.MeshStandardMaterial).color.setHex(
      CELL_STATE_COLOR[roomStates[warehouseId] ?? 'EMPTY'],
    );
  });
}

/** 진행 중인 피킹 위치를 은은하게 깜빡인다(선택된 칸은 제외). */
export function pulsePicking(
  locationMeshes: Map<string, THREE.Mesh>,
  activePicking: Record<string, ActivePicking>,
  selectedCode: string | null,
) {
  const pulse = 0.45 + 0.35 * Math.sin(performance.now() / 250);
  locationMeshes.forEach((mesh, code) => {
    if (!activePicking[code] || code === selectedCode) return;
    const material = mesh.material as THREE.MeshStandardMaterial;
    material.emissive.setHex(PICKING_COLOR);
    material.emissiveIntensity = pulse;
  });
}
