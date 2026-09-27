import { useMemo, useState } from 'react';
import CellDetail from '@/components/warehouse3d/CellDetail';
import CellLegend from '@/components/warehouse3d/CellLegend';
import CellTooltip from '@/components/warehouse3d/CellTooltip';
import WarehouseSummaryPanel from '@/components/warehouse3d/WarehouseSummaryPanel';
import WorldScene from '@/components/warehouse3d/WorldScene';
import ZoneTooltip from '@/components/warehouse3d/ZoneTooltip';
import { useFetch } from '@/hooks/useFetch';
import { useLogisticsEvents } from '@/hooks/useLogisticsEvents';
import { getStocks } from '@/services/stockService';
import { getPickingTasks } from '@/services/workService';
import { ActivePicking, CellState, HoverInfo } from '@/types/warehouse3d';
import { WarehouseNode, ZoneNode } from '@/types/warehouse';
import { aggregateStocks, summarize, summarizeZone, worstCellState } from '@/utils/stockCells';
import { layoutWorld } from '@/utils/worldLayout';
import { zoneHoverCode } from '@/utils/warehouseLayout';

/** 창고를 방으로 보여 주고, 방을 클릭하면 그 창고의 실제 구역·위치가 있는 자리로 카메라가 다가간다. */
function Warehouse3DView({ tree }: { tree: WarehouseNode[] }) {
  const stocks = useFetch(() => getStocks({}));
  const picking = useFetch(() => getPickingTasks('IN_PROGRESS'));
  useLogisticsEvents(() => {
    stocks.reload();
    picking.reload();
  });

  const layout = useMemo(() => layoutWorld(tree), [tree]);
  const cells = useMemo(() => aggregateStocks(stocks.data ?? []), [stocks.data]);
  const activePicking = useMemo<Record<string, ActivePicking>>(
    () =>
      Object.fromEntries(
        (picking.data ?? []).map((t) => [
          t.locationCode,
          { taskNo: t.taskNo, orderNo: t.orderNo, productName: t.productName },
        ]),
      ),
    [picking.data],
  );
  const roomStates = useMemo<Record<number, CellState>>(
    () =>
      Object.fromEntries(
        tree.map((w) => [
          w.id,
          worstCellState(
            w.zones.flatMap((z) => z.locations.map((l) => l.code)),
            cells,
          ),
        ]),
      ),
    [tree, cells],
  );
  const [focused, setFocused] = useState<number | null>(
    tree.length <= 1 ? (tree[0]?.id ?? null) : null,
  );
  const focusedCodes = useMemo(() => {
    const warehouse = tree.find((w) => w.id === focused);
    return warehouse ? warehouse.zones.flatMap((z) => z.locations.map((l) => l.code)) : null;
  }, [tree, focused]);
  const summary = useMemo(() => {
    if (!focusedCodes) return summarize(cells, layout.boxes.length);
    const scoped = Object.fromEntries(
      focusedCodes.filter((c) => cells[c]).map((c) => [c, cells[c]]),
    );
    return summarize(scoped, focusedCodes.length);
  }, [cells, layout.boxes.length, focusedCodes]);
  const zonesByCode = useMemo(() => {
    const map = new Map<string, ZoneNode>();
    tree.forEach((w) => w.zones.forEach((z) => map.set(zoneHoverCode(z.id), z)));
    return map;
  }, [tree]);

  const [hover, setHover] = useState<HoverInfo | null>(null);
  const [selected, setSelected] = useState<string | null>(null);
  const hoveredZone = hover && zonesByCode.get(hover.code);

  if (layout.rooms.length === 0) {
    return (
      <p className="text-sm text-gray-400">
        등록된 창고가 없습니다. 창고·구역·위치를 먼저 등록하세요.
      </p>
    );
  }
  return (
    <div>
      <div className="mb-2 flex flex-wrap items-center justify-between gap-2">
        <div className="flex items-center gap-3">
          {focused !== null && tree.length > 1 && (
            <button className="text-xs text-primary underline" onClick={() => setFocused(null)}>
              ← 전체 창고 보기
            </button>
          )}
          <CellLegend />
        </div>
        <span className="text-xs text-gray-400">
          {focused === null
            ? '방을 클릭하면 그 창고 안으로 확대됩니다.'
            : '드래그: 회전 · 휠: 확대/축소 · 우클릭 드래그: 이동 · 클릭: 상세'}
        </span>
      </div>
      <WarehouseSummaryPanel summary={summary} />
      <div className="relative mt-2 h-[520px] overflow-hidden rounded-md border border-gray-200">
        <WorldScene
          layout={layout}
          cells={cells}
          activePicking={activePicking}
          roomStates={roomStates}
          focusedWarehouseId={focused}
          selectedCode={selected}
          onHover={setHover}
          onSelectLocation={setSelected}
          onSelectRoom={setFocused}
        />
        {hover && hoveredZone && (
          <ZoneTooltip hover={hover} summary={summarizeZone(hoveredZone, cells)} />
        )}
        {hover && !hoveredZone && !hover.code.startsWith('room:') && (
          <CellTooltip hover={hover} cell={cells[hover.code]} picking={activePicking[hover.code]} />
        )}
      </div>
      {selected && (
        <CellDetail
          code={selected}
          cell={cells[selected]}
          picking={activePicking[selected]}
          onClose={() => setSelected(null)}
        />
      )}
    </div>
  );
}

export default Warehouse3DView;
