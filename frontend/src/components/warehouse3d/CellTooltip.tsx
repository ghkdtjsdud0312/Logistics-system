import { CELL_STATE_LABEL } from '@/constants/cellState';
import { HoverInfo, StockCell } from '@/types/warehouse3d';
import { cellState } from '@/utils/stockCells';

/** 마우스를 올린 칸의 요약 정보 */
function CellTooltip({ hover, cell }: { hover: HoverInfo; cell?: StockCell }) {
  return (
    <div
      className="pointer-events-none absolute z-10 rounded-md bg-gray-900/90 px-3 py-2 text-xs text-white shadow"
      style={{ left: hover.x + 14, top: hover.y + 14 }}
    >
      <div className="font-semibold">{hover.code}</div>
      <div>{CELL_STATE_LABEL[cellState(cell)]}</div>
      {cell && (
        <div className="mt-1 text-gray-300">
          현재 {cell.onHand} · 예약 {cell.reserved} · 가용 {cell.available}
        </div>
      )}
    </div>
  );
}

export default CellTooltip;
