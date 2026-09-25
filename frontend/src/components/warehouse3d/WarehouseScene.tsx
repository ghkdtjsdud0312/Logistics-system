import { useEffect, useRef, useState } from 'react';
import * as THREE from 'three';
import { buildWarehouse } from '@/components/warehouse3d/three/buildWarehouse';
import { createSceneKit, fitCamera } from '@/components/warehouse3d/three/sceneSetup';
import { CELL_STATE_COLOR } from '@/constants/cellState';
import { HoverInfo, StockCell, WarehouseLayout } from '@/types/warehouse3d';
import { cellState } from '@/utils/stockCells';
import { sceneBounds } from '@/utils/warehouseLayout';

interface WarehouseSceneProps {
  layout: WarehouseLayout;
  cells: Record<string, StockCell>;
  selectedCode: string | null;
  onHover: (hover: HoverInfo | null) => void;
  onSelect: (code: string | null) => void;
}

const CLICK_TOLERANCE_PX = 4;

/** three.js 장면을 그리는 캔버스. 배치가 바뀌면 다시 만들고, 재고·선택이 바뀌면 색만 바꾼다. */
function WarehouseScene({ layout, cells, selectedCode, onHover, onSelect }: WarehouseSceneProps) {
  const mountRef = useRef<HTMLDivElement>(null);
  const meshesRef = useRef<Map<string, THREE.Mesh>>(new Map());
  const callbacks = useRef({ onHover, onSelect });
  callbacks.current = { onHover, onSelect };
  const [failed, setFailed] = useState(false);

  useEffect(() => {
    const mount = mountRef.current;
    if (!mount) return undefined;
    let kit: ReturnType<typeof createSceneKit>;
    try {
      kit = createSceneKit(mount);
    } catch {
      setFailed(true);
      return undefined;
    }
    const built = buildWarehouse(kit.scene, layout);
    meshesRef.current = built.meshes;
    fitCamera(kit, sceneBounds(layout));

    const raycaster = new THREE.Raycaster();
    const pointer = new THREE.Vector2();
    const pick = (event: PointerEvent | MouseEvent): HoverInfo | null => {
      const rect = kit.renderer.domElement.getBoundingClientRect();
      pointer.set(
        ((event.clientX - rect.left) / rect.width) * 2 - 1,
        -((event.clientY - rect.top) / rect.height) * 2 + 1,
      );
      raycaster.setFromCamera(pointer, kit.camera);
      const hit = raycaster.intersectObjects([...built.meshes.values()], false)[0];
      return hit
        ? {
            code: hit.object.userData.code as string,
            x: event.clientX - rect.left,
            y: event.clientY - rect.top,
          }
        : null;
    };

    let downAt = { x: 0, y: 0 };
    const element = kit.renderer.domElement;
    const onMove = (e: PointerEvent) => callbacks.current.onHover(pick(e));
    const onDown = (e: PointerEvent) => (downAt = { x: e.clientX, y: e.clientY });
    const onClick = (e: MouseEvent) => {
      if (Math.hypot(e.clientX - downAt.x, e.clientY - downAt.y) > CLICK_TOLERANCE_PX) return;
      callbacks.current.onSelect(pick(e)?.code ?? null);
    };
    element.addEventListener('pointermove', onMove);
    element.addEventListener('pointerdown', onDown);
    element.addEventListener('click', onClick);

    let frame = 0;
    const loop = () => {
      kit.controls.update();
      kit.renderer.render(kit.scene, kit.camera);
      frame = requestAnimationFrame(loop);
    };
    loop();

    return () => {
      cancelAnimationFrame(frame);
      element.removeEventListener('pointermove', onMove);
      element.removeEventListener('pointerdown', onDown);
      element.removeEventListener('click', onClick);
      built.dispose();
      kit.dispose();
      meshesRef.current = new Map();
    };
  }, [layout]);

  useEffect(() => {
    meshesRef.current.forEach((mesh, code) => {
      const material = mesh.material as THREE.MeshStandardMaterial;
      material.color.setHex(CELL_STATE_COLOR[cellState(cells[code])]);
      material.emissive.setHex(code === selectedCode ? 0x1d4ed8 : 0x000000);
      material.emissiveIntensity = 0.6;
    });
  }, [cells, selectedCode, layout]);

  if (failed) {
    return (
      <p className="p-6 text-sm text-red-500">이 브라우저에서는 3D(WebGL)를 사용할 수 없습니다.</p>
    );
  }
  return <div ref={mountRef} className="h-full w-full" />;
}

export default WarehouseScene;
