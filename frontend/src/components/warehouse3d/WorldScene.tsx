import { useEffect, useRef, useState } from 'react';
import * as THREE from 'three';
import { buildWorld } from '@/components/warehouse3d/three/buildWorld';
import { createRoomHoverTracker } from '@/components/warehouse3d/three/roomHoverHighlight';
import { bindPointerEvents, createPicker } from '@/components/warehouse3d/three/scenePicking';
import { pulsePicking, recolorScene } from '@/components/warehouse3d/three/sceneRecolor';
import {
  animateCameraTo,
  createSceneKit,
  fitCamera,
  SceneKit,
} from '@/components/warehouse3d/three/sceneSetup';
import { ActivePicking, CellState, HoverInfo, StockCell, WorldLayout } from '@/types/warehouse3d';
import { worldBounds } from '@/utils/worldLayout';

interface WorldSceneProps {
  layout: WorldLayout;
  cells: Record<string, StockCell>;
  activePicking: Record<string, ActivePicking>;
  roomStates: Record<number, CellState>;
  focusedWarehouseId: number | null;
  selectedCode: string | null;
  onHover: (hover: HoverInfo | null) => void;
  onSelectLocation: (code: string | null) => void;
  onSelectRoom: (warehouseId: number) => void;
}

const ZOOM_DURATION_MS = 550;

/** 창고를 방으로, 그 안의 실제 구역·위치를 한 장면에 담는다. 방을 클릭하면 카메라가 그 방으로 다가간다. */
function WorldScene({
  layout,
  cells,
  activePicking,
  roomStates,
  focusedWarehouseId,
  selectedCode,
  onHover,
  onSelectLocation,
  onSelectRoom,
}: WorldSceneProps) {
  const mountRef = useRef<HTMLDivElement>(null);
  const kitRef = useRef<SceneKit | null>(null);
  const meshesRef = useRef({
    locationMeshes: new Map<string, THREE.Mesh>(),
    zoneMeshes: new Map<string, THREE.Mesh>(),
    roomMeshes: new Map<string, THREE.Mesh>(),
    rings: new Map<string, THREE.LineSegments>(),
  });
  const activePickingRef = useRef(activePicking);
  activePickingRef.current = activePicking;
  const selectedCodeRef = useRef(selectedCode);
  selectedCodeRef.current = selectedCode;
  const callbacks = useRef({ onHover, onSelectLocation, onSelectRoom });
  callbacks.current = { onHover, onSelectLocation, onSelectRoom };
  const firstFocus = useRef(true);
  const cancelZoomRef = useRef<() => void>(() => {});
  const [failed, setFailed] = useState(false);

  useEffect(() => {
    const mount = mountRef.current;
    if (!mount) return undefined;
    let kit: SceneKit;
    try {
      kit = createSceneKit(mount);
    } catch {
      setFailed(true);
      return undefined;
    }
    kitRef.current = kit;
    const built = buildWorld(kit.scene, layout);
    meshesRef.current = {
      locationMeshes: built.locationMeshes,
      zoneMeshes: built.zoneMeshes,
      roomMeshes: built.roomMeshes,
      rings: built.rings,
    };
    fitCamera(kit, worldBounds(layout));

    const pickable = new Map<string, THREE.Object3D>([
      ...built.locationMeshes,
      ...built.zoneMeshes,
      ...built.roomMeshes,
      ...built.badges,
    ]);
    const pick = createPicker(kit.renderer, kit.camera, pickable);
    const trackRoomHover = createRoomHoverTracker(built.roomMeshes);
    const unbind = bindPointerEvents(
      kit.renderer.domElement,
      pick,
      (hover) => {
        trackRoomHover(hover);
        callbacks.current.onHover(hover);
      },
      (code) => {
        if (!code) return;
        if (code.startsWith('room:')) {
          callbacks.current.onSelectRoom(Number(code.slice('room:'.length)));
        } else if (!code.startsWith('zone:')) {
          callbacks.current.onSelectLocation(code);
        }
      },
    );

    let frame = 0;
    const loop = () => {
      kit.controls.update();
      pulsePicking(
        meshesRef.current.locationMeshes,
        activePickingRef.current,
        selectedCodeRef.current,
      );
      kit.renderer.render(kit.scene, kit.camera);
      frame = requestAnimationFrame(loop);
    };
    loop();

    return () => {
      cancelAnimationFrame(frame);
      cancelZoomRef.current();
      unbind();
      built.dispose();
      kit.dispose();
      kitRef.current = null;
    };
  }, [layout]);

  // 재고·선택 상태가 바뀌어도 장면을 다시 만들지 않고 색만 바꾼다.
  useEffect(() => {
    recolorScene(meshesRef.current, cells, activePicking, selectedCode, roomStates);
  }, [cells, activePicking, selectedCode, roomStates, layout]);

  // 확대 대상(focusedWarehouseId)이 바뀌면 카메라를 그리로 옮긴다. 처음 뜰 때는 애니메이션 없이 바로 맞춘다.
  useEffect(() => {
    const kit = kitRef.current;
    if (!kit) return;
    const room = layout.rooms.find((r) => r.warehouseId === focusedWarehouseId);
    const bounds = room ? room.closeup : worldBounds(layout);
    if (firstFocus.current) {
      firstFocus.current = false;
      fitCamera(kit, bounds);
    } else {
      cancelZoomRef.current();
      cancelZoomRef.current = animateCameraTo(kit, bounds, ZOOM_DURATION_MS);
    }
  }, [focusedWarehouseId, layout]);

  if (failed) {
    return (
      <p className="p-6 text-sm text-red-500">이 브라우저에서는 3D(WebGL)를 사용할 수 없습니다.</p>
    );
  }
  return <div ref={mountRef} className="h-full w-full" />;
}

export default WorldScene;
