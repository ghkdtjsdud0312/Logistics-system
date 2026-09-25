/** 선택 목록에서 id를 넣거나 뺀 새 배열을 돌려준다. */
export function toggleId(selected: number[], id: number): number[] {
  return selected.includes(id) ? selected.filter((v) => v !== id) : [...selected, id];
}
