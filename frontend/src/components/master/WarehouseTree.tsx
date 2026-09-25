import { WarehouseNode } from '@/types/warehouse';

/** 창고 → 구역 → 위치 트리(일반 UI). 3D 시각화는 이 데이터를 나중에 사용한다. */
function WarehouseTree({ tree }: { tree: WarehouseNode[] }) {
  if (tree.length === 0) {
    return <p className="text-sm text-gray-400">등록된 창고가 없습니다.</p>;
  }
  return (
    <ul className="space-y-2 rounded-md border border-gray-200 bg-white p-4 text-sm">
      {tree.map((w) => (
        <li key={w.id}>
          <span className="font-semibold">{w.name}</span>
          <ul className="ml-4 mt-1 space-y-1 border-l border-gray-200 pl-3">
            {w.zones.map((z) => (
              <li key={z.id}>
                <span className="text-gray-700">
                  {z.code} {z.name}
                </span>
                <span className="ml-2 text-xs text-gray-500">
                  {z.locations.map((l) => l.code).join(', ')}
                </span>
              </li>
            ))}
          </ul>
        </li>
      ))}
    </ul>
  );
}

export default WarehouseTree;
