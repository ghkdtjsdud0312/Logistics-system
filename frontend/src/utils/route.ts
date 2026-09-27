/** 배열에서 index 항목을 offset(-1/+1)만큼 옮긴 새 배열. 범위를 벗어나면 그대로 돌려준다. */
export function moveItem<T>(items: T[], index: number, offset: number): T[] {
  const target = index + offset;
  if (target < 0 || target >= items.length) return items;
  const next = [...items];
  [next[index], next[target]] = [next[target], next[index]];
  return next;
}

/** 적재 순서: 방문 순서의 역순(마지막에 내릴 것부터 싣는다). */
export function loadingSequence<T>(visitOrder: T[]): T[] {
  return [...visitOrder].reverse();
}
