import RowActions from '@/components/common/RowActions';
import ZoneBlock from '@/components/master/ZoneBlock';
import { EditTarget, WarehouseNode } from '@/types/warehouse';

interface WarehouseCardsProps {
  tree: WarehouseNode[];
  onEdit: (t: EditTarget) => void;
  onDelete: (t: EditTarget) => void;
}

/** 창고별 카드: 카드 안에 그 창고의 구역과 위치가 모여 보인다. */
function WarehouseCards({ tree, onEdit, onDelete }: WarehouseCardsProps) {
  if (tree.length === 0) {
    return <p className="text-sm text-gray-400">등록된 창고가 없습니다.</p>;
  }
  return (
    <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
      {tree.map((w) => (
        <section key={w.id} className="rounded-md border border-gray-200 bg-white p-4">
          <div className="mb-3 flex items-center justify-between gap-2">
            <h3 className="min-w-0 truncate font-semibold" title={`${w.name} (${w.code})`}>
              {w.name} <span className="text-xs font-normal text-gray-400">{w.code}</span>
            </h3>
            <RowActions
              onEdit={() => onEdit({ kind: 'warehouse', id: w.id, name: w.name })}
              onDelete={() => onDelete({ kind: 'warehouse', id: w.id, name: w.name })}
            />
          </div>
          <div className="space-y-2">
            {w.zones.length === 0 && (
              <p className="text-xs text-gray-400">등록된 구역이 없습니다.</p>
            )}
            {w.zones.map((z) => (
              <ZoneBlock key={z.id} zone={z} onEdit={onEdit} onDelete={onDelete} />
            ))}
          </div>
        </section>
      ))}
    </div>
  );
}

export default WarehouseCards;
