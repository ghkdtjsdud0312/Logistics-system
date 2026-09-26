import RowActions from '@/components/common/RowActions';
import { EditTarget, ZoneNode } from '@/types/warehouse';

interface ZoneBlockProps {
  zone: ZoneNode;
  onEdit: (t: EditTarget) => void;
  onDelete: (t: EditTarget) => void;
}

/** 창고 카드 안의 구역 한 칸: 구역 정보와 등록된 위치 칩 */
function ZoneBlock({ zone, onEdit, onDelete }: ZoneBlockProps) {
  const target = (kind: EditTarget['kind'], id: number, name: string): EditTarget => ({
    kind,
    id,
    name,
  });

  return (
    <div className="rounded border border-gray-100 bg-gray-50 p-2">
      <div className="flex items-center justify-between gap-2">
        <span
          className="min-w-0 truncate text-sm text-gray-700"
          title={`${zone.code} ${zone.name}`}
        >
          {zone.code} {zone.name}
        </span>
        <RowActions
          onEdit={() => onEdit(target('zone', zone.id, zone.name))}
          onDelete={() => onDelete(target('zone', zone.id, zone.name))}
        />
      </div>
      <div className="mt-2 flex flex-wrap gap-1">
        {zone.locations.length === 0 && <span className="text-xs text-gray-400">위치 없음</span>}
        {zone.locations.map((l) => (
          <span
            key={l.id}
            className="inline-flex items-center gap-1 rounded-full bg-white px-2 py-0.5 text-xs text-gray-600 ring-1 ring-gray-200"
          >
            {l.code}
            <button
              title="위치 삭제"
              className="text-gray-400 hover:text-red-600"
              onClick={() => onDelete(target('location', l.id, l.code))}
            >
              ✕
            </button>
          </span>
        ))}
      </div>
    </div>
  );
}

export default ZoneBlock;
