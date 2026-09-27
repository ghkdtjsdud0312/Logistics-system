import * as THREE from 'three';
import { buildWarehouse } from '@/components/warehouse3d/three/buildWarehouse';
import { makeBadge, makeNameLabel } from '@/components/warehouse3d/three/campusDecor';
import { CELL_STATE_COLOR } from '@/constants/cellState';
import { WorldLayout } from '@/types/warehouse3d';
import { roomHoverCode } from '@/utils/warehouseLayout';

export interface BuiltWorld {
  /** 위치 코드 → 선반 칸 메시 */
  locationMeshes: Map<string, THREE.Mesh>;
  /** zoneHoverCode(zoneId) → 구역 바닥판 메시 */
  zoneMeshes: Map<string, THREE.Mesh>;
  /** roomHoverCode(warehouseId) → 방 바닥판(클릭하면 그 방으로 확대한다) */
  roomMeshes: Map<string, THREE.Mesh>;
  /** roomHoverCode(warehouseId) → 번호 배지(바닥과 함께 클릭할 수 있다) */
  badges: Map<string, THREE.Sprite>;
  /** 위치 코드 → 최근 변경 표식(기본은 숨김) */
  rings: Map<string, THREE.LineSegments>;
  dispose: () => void;
}

/** 창고마다 방 바닥·번호·이름표를 만들고, 그 위에 실제 구역·위치(buildWarehouse)를 놓는다. */
export function buildWorld(scene: THREE.Scene, layout: WorldLayout): BuiltWorld {
  const roomGroup = new THREE.Group();
  const roomMeshes = new Map<string, THREE.Mesh>();
  const badges = new Map<string, THREE.Sprite>();
  const roomFloorGeometry = new THREE.BoxGeometry(1, 0.2, 1);

  layout.rooms.forEach((room) => {
    const code = roomHoverCode(room.warehouseId);
    const floor = new THREE.Mesh(
      roomFloorGeometry,
      new THREE.MeshStandardMaterial({ color: CELL_STATE_COLOR.EMPTY }),
    );
    floor.scale.set(room.size, 1, room.size);
    floor.position.set(room.x, -0.3, room.z);
    floor.userData = { code };
    roomGroup.add(floor);
    roomMeshes.set(code, floor);

    const badge = makeBadge(room.number);
    badge.position.set(room.x - room.size / 2 + 0.9, 1.1, room.z - room.size / 2 + 0.9);
    badge.userData = { code };
    roomGroup.add(badge);
    badges.set(code, badge);

    const label = makeNameLabel(room.name);
    label.position.set(room.x, 0.05, room.z + room.size / 2 - 0.6);
    roomGroup.add(label);
  });
  scene.add(roomGroup);

  const content = buildWarehouse(scene, { boxes: layout.boxes, slabs: layout.slabs });

  return {
    locationMeshes: content.meshes,
    zoneMeshes: content.zoneMeshes,
    rings: content.rings,
    roomMeshes,
    badges,
    dispose: () => {
      scene.remove(roomGroup);
      roomFloorGeometry.dispose();
      roomGroup.traverse((object) => {
        if (object instanceof THREE.Mesh && object.geometry !== roomFloorGeometry) {
          object.geometry.dispose();
        }
        if (object instanceof THREE.Mesh || object instanceof THREE.Sprite) {
          const material = object.material as THREE.Material & { map?: THREE.Texture | null };
          material.map?.dispose();
          material.dispose();
        }
      });
      content.dispose();
    },
  };
}
