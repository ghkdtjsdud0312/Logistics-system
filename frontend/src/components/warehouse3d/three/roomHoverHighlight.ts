import * as THREE from 'three';
import { HoverInfo } from '@/types/warehouse3d';

const ROOM_HOVER_COLOR = 0x1d4ed8;

/** 마우스가 방(room:) 위에 있으면 그 방 바닥을 옅은 파란색으로, 벗어나면 원래대로 되돌린다. */
export function createRoomHoverTracker(roomMeshes: Map<string, THREE.Mesh>) {
  let hovered: string | null = null;
  return (hover: HoverInfo | null) => {
    if (hovered && hovered !== hover?.code) {
      const material = roomMeshes.get(hovered)?.material as THREE.MeshStandardMaterial;
      material?.emissive.setHex(0x000000);
    }
    hovered = hover?.code.startsWith('room:') ? hover.code : null;
    if (hovered) {
      const material = roomMeshes.get(hovered)?.material as THREE.MeshStandardMaterial;
      material?.emissive.setHex(ROOM_HOVER_COLOR);
      material.emissiveIntensity = 0.3;
    }
  };
}
