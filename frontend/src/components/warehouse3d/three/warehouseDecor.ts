import * as THREE from 'three';

/** 구역 이름 표지판(스프라이트) */
export function makeLabel(text: string): THREE.Sprite {
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
