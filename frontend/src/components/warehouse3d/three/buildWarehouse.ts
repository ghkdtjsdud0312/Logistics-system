import * as THREE from 'three';
import { CELL_STATE_COLOR } from '@/constants/cellState';
import { WarehouseLayout, ZoneSlab } from '@/types/warehouse3d';
import { zoneHoverCode } from '@/utils/warehouseLayout';
import { makeLabel } from '@/components/warehouse3d/three/warehouseDecor';

export interface BuiltWarehouse {
  /** 위치 코드 → 선반 칸 메시 */
  meshes: Map<string, THREE.Mesh>;
  /** 위치 코드 → 최근 변경 표식(기본은 숨김) */
  rings: Map<string, THREE.LineSegments>;
  /** zoneHoverCode(zoneId) → 구역 바닥판 메시(마우스를 올리면 구역 요약을 보여 준다) */
  zoneMeshes: Map<string, THREE.Mesh>;
  dispose: () => void;
}

function addSlab(group: THREE.Group, slab: ZoneSlab, zoneMeshes: Map<string, THREE.Mesh>) {
  const floor = new THREE.Mesh(
    new THREE.BoxGeometry(slab.width, 0.1, slab.depth),
    new THREE.MeshStandardMaterial({ color: 0xe2e8f0 }),
  );
  floor.position.set(slab.x, -0.05, slab.z);
  floor.userData = { code: zoneHoverCode(slab.zoneId) };
  group.add(floor);
  zoneMeshes.set(zoneHoverCode(slab.zoneId), floor);
  const label = makeLabel(slab.label);
  label.position.set(slab.x, 0.5, slab.z + slab.depth / 2 + 0.9);
  group.add(label);
}

/** 위치 칸과 구역 바닥판을 만들어 장면에 추가한다. 색은 재고에 따라 나중에 바꾼다. */
export function buildWarehouse(scene: THREE.Scene, layout: WarehouseLayout): BuiltWarehouse {
  const group = new THREE.Group();
  const meshes = new Map<string, THREE.Mesh>();
  const rings = new Map<string, THREE.LineSegments>();
  const zoneMeshes = new Map<string, THREE.Mesh>();
  const boxGeometry = new THREE.BoxGeometry(1.3, 1.05, 1.3);
  const ringGeometry = new THREE.EdgesGeometry(new THREE.BoxGeometry(1.42, 1.17, 1.42));
  layout.boxes.forEach((box) => {
    const mesh = new THREE.Mesh(
      boxGeometry,
      new THREE.MeshStandardMaterial({ color: CELL_STATE_COLOR.EMPTY }),
    );
    mesh.position.set(box.x, box.y, box.z);
    mesh.userData = { code: box.code };
    group.add(mesh);
    meshes.set(box.code, mesh);

    const ring = new THREE.LineSegments(
      ringGeometry,
      new THREE.LineBasicMaterial({ color: 0x22d3ee }),
    );
    ring.position.copy(mesh.position);
    ring.visible = false;
    group.add(ring);
    rings.set(box.code, ring);
  });
  layout.slabs.forEach((slab) => addSlab(group, slab, zoneMeshes));
  scene.add(group);

  return {
    meshes,
    rings,
    zoneMeshes,
    dispose: () => {
      scene.remove(group);
      boxGeometry.dispose();
      ringGeometry.dispose();
      group.traverse((object) => {
        if (
          object instanceof THREE.Mesh &&
          object.geometry !== boxGeometry &&
          object.geometry !== ringGeometry
        ) {
          object.geometry.dispose();
        }
        if (
          object instanceof THREE.Mesh ||
          object instanceof THREE.Sprite ||
          object instanceof THREE.LineSegments
        ) {
          const material = object.material as THREE.Material & { map?: THREE.Texture | null };
          material.map?.dispose();
          material.dispose();
        }
      });
    },
  };
}
