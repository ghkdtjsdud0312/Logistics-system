import * as THREE from 'three';
import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls.js';
import { SceneBounds } from '@/types/warehouse3d';

export interface SceneKit {
  scene: THREE.Scene;
  camera: THREE.PerspectiveCamera;
  renderer: THREE.WebGLRenderer;
  controls: OrbitControls;
  dispose: () => void;
}

/** 렌더러, 카메라, 조명, 마우스 조작(OrbitControls)을 만든다. WebGL을 쓸 수 없으면 예외가 난다. */
export function createSceneKit(mount: HTMLElement): SceneKit {
  const scene = new THREE.Scene();
  scene.background = new THREE.Color(0xf1f5f9);
  const camera = new THREE.PerspectiveCamera(45, 1, 0.1, 500);
  const renderer = new THREE.WebGLRenderer({ antialias: true });
  renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2));
  mount.appendChild(renderer.domElement);

  const controls = new OrbitControls(camera, renderer.domElement);
  controls.enableDamping = true;
  controls.maxPolarAngle = Math.PI / 2 - 0.05;

  scene.add(new THREE.AmbientLight(0xffffff, 0.9));
  const sun = new THREE.DirectionalLight(0xffffff, 1.2);
  sun.position.set(10, 20, 15);
  scene.add(sun);

  const resize = () => {
    const { clientWidth: width, clientHeight: height } = mount;
    renderer.setSize(width, height);
    camera.aspect = width / Math.max(height, 1);
    camera.updateProjectionMatrix();
  };
  resize();
  const observer = new ResizeObserver(resize);
  observer.observe(mount);

  return {
    scene,
    camera,
    renderer,
    controls,
    dispose: () => {
      observer.disconnect();
      controls.dispose();
      renderer.dispose();
      mount.removeChild(renderer.domElement);
    },
  };
}

/** 장면 전체가 보이도록 카메라를 비스듬히 뒤로 물린다. */
export function fitCamera(kit: SceneKit, bounds: SceneBounds) {
  const { center, size } = bounds;
  kit.controls.target.set(center.x, center.y, center.z);
  kit.camera.position.set(center.x + size * 0.7, center.y + size * 0.9, center.z + size * 1.3);
  kit.controls.update();
}
