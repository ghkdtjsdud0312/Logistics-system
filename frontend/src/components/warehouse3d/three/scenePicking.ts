import * as THREE from 'three';
import { HoverInfo } from '@/types/warehouse3d';

const CLICK_TOLERANCE_PX = 6;

/** 포인터 위치를 광선으로 쏘아 마우스 아래 칸을 찾는 함수를 만든다. */
export function createPicker(
  renderer: THREE.WebGLRenderer,
  camera: THREE.Camera,
  objects: Map<string, THREE.Object3D>,
) {
  const raycaster = new THREE.Raycaster();
  const pointer = new THREE.Vector2();
  return (event: PointerEvent | MouseEvent): HoverInfo | null => {
    const rect = renderer.domElement.getBoundingClientRect();
    pointer.set(
      ((event.clientX - rect.left) / rect.width) * 2 - 1,
      -((event.clientY - rect.top) / rect.height) * 2 + 1,
    );
    raycaster.setFromCamera(pointer, camera);
    const hit = raycaster.intersectObjects([...objects.values()], false)[0];
    return hit
      ? {
          code: hit.object.userData.code as string,
          x: event.clientX - rect.left,
          y: event.clientY - rect.top,
        }
      : null;
  };
}

/**
 * 캔버스에 hover/click 이벤트를 연결한다. click은 드래그(회전)와 구분해 클릭 오차 이내일 때만 선택한다.
 * 클릭 가능한 대상 위에서는 커서를 포인터 모양으로 바꿔 클릭할 수 있음을 알려 준다.
 */
export function bindPointerEvents(
  element: HTMLElement,
  pick: (event: PointerEvent | MouseEvent) => HoverInfo | null,
  onHover: (hover: HoverInfo | null) => void,
  onSelect: (code: string | null) => void,
) {
  let downAt = { x: 0, y: 0 };
  const onMove = (e: PointerEvent) => {
    const hover = pick(e);
    element.style.cursor = hover ? 'pointer' : 'auto';
    onHover(hover);
  };
  const onDown = (e: PointerEvent) => (downAt = { x: e.clientX, y: e.clientY });
  const onClick = (e: MouseEvent) => {
    if (Math.hypot(e.clientX - downAt.x, e.clientY - downAt.y) > CLICK_TOLERANCE_PX) return;
    onSelect(pick(e)?.code ?? null);
  };
  element.addEventListener('pointermove', onMove);
  element.addEventListener('pointerdown', onDown);
  element.addEventListener('click', onClick);
  return () => {
    element.removeEventListener('pointermove', onMove);
    element.removeEventListener('pointerdown', onDown);
    element.removeEventListener('click', onClick);
    element.style.cursor = 'auto';
  };
}
