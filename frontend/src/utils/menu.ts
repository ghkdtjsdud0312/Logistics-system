import { MenuChild } from '@/types/menu';

/** 메뉴 항목이 가진 모든 경로(하위 메뉴 포함)를 펼쳐 돌려준다. */
export function menuPaths(children: MenuChild[] = []): string[] {
  return children.flatMap((c) => [...(c.to ? [c.to] : []), ...menuPaths(c.children)]);
}
