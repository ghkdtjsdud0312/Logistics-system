import * as THREE from 'three';
import { CELL_STATE_COLOR } from '@/constants/cellState';
import { WarehouseLayout, ZoneSlab } from '@/types/warehouse3d';

export interface BuiltWarehouse {
  /** 위치 코드 → 선반 칸 메시 */
  meshes: Map<string, THREE.Mesh>;
  dispose: () => void;
}

/** 구역 이름 표지판(스프라이트) */
function makeLabel(text: string): THREE.Sprite {
  const canvas = document.createElement('canvas');
  canvas.width = 384;
  canvas.height = 64;
  const context = canvas.getContext('2d');
  if (context) {
    context.font = 'bold 30px sans-serif';
    context.fillStyle = '#334155';
    context.textAlign = 'center';
    context.fillText(text, canvas.width / 2, 42);
  }
  const material = new THREE.SpriteMaterial({
    map: new THREE.CanvasTexture(canvas),
    transparent: true,
  });
  const sprite = new THREE.Sprite(material);
  sprite.scale.set(6, 1, 1);
  return sprite;
}

function addSlab(group: THREE.Group, slab: ZoneSlab) {
  const floor = new THREE.Mesh(
    new THREE.BoxGeometry(slab.width, 0.1, slab.depth),
    new THREE.MeshStandardMaterial({ color: 0xe2e8f0 }),
  );
  floor.position.set(slab.x, -0.05, slab.z);
  group.add(floor);
  const label = makeLabel(slab.label);
  label.position.set(slab.x, 0.5, slab.z + slab.depth / 2 + 0.9);
  group.add(label);
}

/** 위치 칸과 구역 바닥판을 만들어 장면에 추가한다. 색은 재고에 따라 나중에 바꾼다. */
export function buildWarehouse(scene: THREE.Scene, layout: WarehouseLayout): BuiltWarehouse {
  const group = new THREE.Group();
  const meshes = new Map<string, THREE.Mesh>();
  const boxGeometry = new THREE.BoxGeometry(1.3, 1.05, 1.3);
  layout.boxes.forEach((box) => {
    const mesh = new THREE.Mesh(
      boxGeometry,
      new THREE.MeshStandardMaterial({ color: CELL_STATE_COLOR.EMPTY }),
    );
    mesh.position.set(box.x, box.y, box.z);
    mesh.userData = { code: box.code };
    group.add(mesh);
    meshes.set(box.code, mesh);
  });
  layout.slabs.forEach((slab) => addSlab(group, slab));
  scene.add(group);

  return {
    meshes,
    dispose: () => {
      scene.remove(group);
      boxGeometry.dispose();
      group.traverse((object) => {
        if (object instanceof THREE.Mesh && object.geometry !== boxGeometry)
          object.geometry.dispose();
        if (object instanceof THREE.Mesh || object instanceof THREE.Sprite) {
          const material = object.material as THREE.Material & { map?: THREE.Texture | null };
          material.map?.dispose();
          material.dispose();
        }
      });
    },
  };
}
