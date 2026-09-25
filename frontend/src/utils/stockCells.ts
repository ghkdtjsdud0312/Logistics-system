import { CellState, StockCell } from '@/types/warehouse3d';
import { StockRow } from '@/types/stock';

/** 재고 행(위치×상품)을 위치 코드별 합계와 상품 내역으로 묶는다. */
export function aggregateStocks(rows: StockRow[]): Record<string, StockCell> {
  const cells: Record<string, StockCell> = {};
  rows.forEach((r) => {
    const cell = (cells[r.locationCode] ??= { onHand: 0, reserved: 0, available: 0, products: [] });
    cell.onHand += r.onHand;
    cell.reserved += r.reserved;
    cell.available += r.available;
    cell.products.push({
      name: r.productName,
      onHand: r.onHand,
      reserved: r.reserved,
      available: r.available,
    });
  });
  return cells;
}

/** 칸의 표시 상태: 재고 없음 → 소진 → 예약 있음 → 가용 순으로 판단한다. */
export function cellState(cell?: StockCell): CellState {
  if (!cell || cell.onHand === 0) return 'EMPTY';
  if (cell.available <= 0) return 'SOLD_OUT';
  return cell.reserved > 0 ? 'RESERVED' : 'AVAILABLE';
}
