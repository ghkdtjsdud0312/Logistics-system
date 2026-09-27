import { CELL_STATE_LABEL } from '@/constants/cellState';
import { ActivePicking, StockCell } from '@/types/warehouse3d';
import { cellState } from '@/utils/stockCells';
import { formatRelativeTime } from '@/utils/format';

/** 클릭해서 선택한 칸의 상품별 재고 */
function CellDetail({
  code,
  cell,
  picking,
  onClose,
}: {
  code: string;
  cell?: StockCell;
  picking?: ActivePicking;
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
      {cell && (
        <p className="mb-2 text-xs text-gray-400">
          최근 변경: {formatRelativeTime(cell.updatedAt)}
        </p>
      )}
      {picking && (
        <p className="mb-2 rounded bg-amber-50 px-2 py-1 text-xs text-amber-700">
          피킹 진행 중 · {picking.taskNo} · {picking.orderNo} · {picking.productName}
        </p>
      )}
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
