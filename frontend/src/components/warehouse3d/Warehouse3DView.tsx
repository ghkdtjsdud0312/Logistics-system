import { useMemo, useState } from 'react';
import CellDetail from '@/components/warehouse3d/CellDetail';
import CellLegend from '@/components/warehouse3d/CellLegend';
import CellTooltip from '@/components/warehouse3d/CellTooltip';
import WarehouseScene from '@/components/warehouse3d/WarehouseScene';
import { useFetch } from '@/hooks/useFetch';
import { useLogisticsEvents } from '@/hooks/useLogisticsEvents';
import { getStocks } from '@/services/stockService';
import { HoverInfo } from '@/types/warehouse3d';
import { WarehouseNode } from '@/types/warehouse';
import { aggregateStocks } from '@/utils/stockCells';
import { layoutWarehouses } from '@/utils/warehouseLayout';

/** 3D 창고: 위치 칸을 재고 상태 색으로 보여 주고 상태가 바뀌면 SSE로 갱신한다. */
function Warehouse3DView({ tree }: { tree: WarehouseNode[] }) {
  const stocks = useFetch(() => getStocks({}));
  useLogisticsEvents(() => stocks.reload());
  const layout = useMemo(() => layoutWarehouses(tree), [tree]);
  const cells = useMemo(() => aggregateStocks(stocks.data ?? []), [stocks.data]);
  const [hover, setHover] = useState<HoverInfo | null>(null);
  const [selected, setSelected] = useState<string | null>(null);

  if (layout.boxes.length === 0) {
    return (
      <p className="text-sm text-gray-400">
        등록된 위치가 없습니다. 창고·구역·위치를 먼저 등록하세요.
      </p>
    );
  }
  return (
    <div>
      <div className="mb-2 flex flex-wrap items-center justify-between gap-2">
        <CellLegend />
        <span className="text-xs text-gray-400">
          드래그: 회전 · 휠: 확대/축소 · 우클릭 드래그: 이동 · 클릭: 상세
        </span>
      </div>
      <div className="relative h-[520px] overflow-hidden rounded-md border border-gray-200">
        <WarehouseScene
          layout={layout}
          cells={cells}
          selectedCode={selected}
          onHover={setHover}
          onSelect={setSelected}
        />
        {hover && <CellTooltip hover={hover} cell={cells[hover.code]} />}
      </div>
      {selected && (
        <CellDetail code={selected} cell={cells[selected]} onClose={() => setSelected(null)} />
      )}
    </div>
  );
}

export default Warehouse3DView;
