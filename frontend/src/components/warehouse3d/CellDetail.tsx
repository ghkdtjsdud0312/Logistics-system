import { CELL_STATE_LABEL } from '@/constants/cellState';
import { StockCell } from '@/types/warehouse3d';
import { cellState } from '@/utils/stockCells';

/** 클릭해서 선택한 칸의 상품별 재고 */
function CellDetail({
  code,
  cell,
  onClose,
}: {
  code: string;
  cell?: StockCell;
  onClose: () => void;
}) {
  return (
    <div className="mt-3 rounded-md border border-gray-200 bg-white p-4 text-sm">
      <div className="mb-2 flex items-center justify-between">
        <span className="font-semibold">
          {code}{' '}
          <span className="ml-2 text-xs font-normal text-gray-500">
            {CELL_STATE_LABEL[cellState(cell)]}
          </span>
        </span>
        <button className="text-xs text-gray-400" onClick={onClose}>
          닫기 ✕
        </button>
      </div>
      {!cell || cell.products.length === 0 ? (
        <p className="text-gray-400">이 위치에는 재고가 없습니다.</p>
      ) : (
        <ul className="space-y-1">
          {cell.products.map((p) => (
            <li key={p.name} className="flex justify-between">
              <span>{p.name}</span>
              <span className="tabular-nums text-gray-600">
                현재 {p.onHand} · 예약 {p.reserved} · 가용 {p.available}
              </span>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}

export default CellDetail;
