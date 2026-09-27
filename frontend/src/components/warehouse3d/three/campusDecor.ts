import * as THREE from 'three';

/** 어두운 원판 + 흰 숫자 배지(사진 속 번호 표시와 같은 모양) */
export function makeBadge(number: number): THREE.Sprite {
  const canvas = document.createElement('canvas');
  canvas.width = 128;
  canvas.height = 128;
  const context = canvas.getContext('2d');
  if (context) {
    context.fillStyle = '#1e293b';
    context.beginPath();
    context.arc(64, 64, 58, 0, Math.PI * 2);
    context.fill();
    context.fillStyle = '#ffffff';
    context.font = 'bold 56px sans-serif';
    context.textAlign = 'center';
    context.textBaseline = 'middle';
    context.fillText(String(number), 64, 68);
  }
  const sprite = new THREE.Sprite(
    new THREE.SpriteMaterial({ map: new THREE.CanvasTexture(canvas), transparent: true }),
  );
  sprite.scale.set(1.1, 1.1, 1);
  return sprite;
}

/** 방 아래에 붙는 창고 이름 표지판(스프라이트) */
export function makeNameLabel(text: string): THREE.Sprite {
  const canvas = document.createElement('canvas');
  canvas.width = 384;
  canvas.height = 64;
  const context = canvas.getContext('2d');
  if (context) {
    context.font = 'bold 30px sans-serif';
    context.fillStyle = '#1e293b';
    context.textAlign = 'center';
    context.fillText(text, canvas.width / 2, 42);
  }
  const sprite = new THREE.Sprite(
    new THREE.SpriteMaterial({ map: new THREE.CanvasTexture(canvas), transparent: true }),
  );
  sprite.scale.set(4.5, 0.75, 1);
  return sprite;
}
