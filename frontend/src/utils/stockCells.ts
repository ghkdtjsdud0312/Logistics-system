import { CellState, StockCell, WarehouseSummary, ZoneSummary } from '@/types/warehouse3d';
import { StockRow } from '@/types/stock';
import { ZoneNode } from '@/types/warehouse';

/** 재고 행(위치×상품)을 위치 코드별 합계와 상품 내역으로 묶는다. */
export function aggregateStocks(rows: StockRow[]): Record<string, StockCell> {
  const cells: Record<string, StockCell> = {};
  rows.forEach((r) => {
    const cell = (cells[r.locationCode] ??= {
      onHand: 0,
      reserved: 0,
      available: 0,
      updatedAt: r.updatedAt,
      products: [],
    });
    cell.onHand += r.onHand;
    cell.reserved += r.reserved;
    cell.available += r.available;
    if (r.updatedAt > cell.updatedAt) cell.updatedAt = r.updatedAt;
    cell.products.push({
      name: r.productName,
      onHand: r.onHand,
      reserved: r.reserved,
      available: r.available,
    });
  });
  return cells;
}

const RECENT_WINDOW_MS = 24 * 60 * 60 * 1000;

/** 최근 24시간 안에 재고가 바뀐 칸인지 */
export function isRecentlyChanged(cell: StockCell | undefined, now = Date.now()): boolean {
  return cell !== undefined && now - new Date(cell.updatedAt).getTime() < RECENT_WINDOW_MS;
}

/** 창고 요약: 전체 칸 수 대비 상태별 칸 수 */
export function summarize(cells: Record<string, StockCell>, totalCells: number): WarehouseSummary {
  const states = Object.values(cells).map((c) => cellState(c));
  return {
    totalCells,
    emptyCells: totalCells - states.length,
    soldOutCells: states.filter((s) => s === 'SOLD_OUT').length,
    reservedCells: states.filter((s) => s === 'RESERVED').length,
    availableCells: states.filter((s) => s === 'AVAILABLE').length,
  };
}

/** 구역 하나(위치 여러 개)의 재고를 합친 요약 */
export function summarizeZone(zone: ZoneNode, cells: Record<string, StockCell>): ZoneSummary {
  const zoneCells = zone.locations.map((l) => cells[l.code]);
  const states = zoneCells.map((c) => cellState(c));
  return {
    zoneCode: zone.code,
    locationCount: zone.locations.length,
    onHand: zoneCells.reduce((sum, c) => sum + (c?.onHand ?? 0), 0),
    reserved: zoneCells.reduce((sum, c) => sum + (c?.reserved ?? 0), 0),
    available: zoneCells.reduce((sum, c) => sum + (c?.available ?? 0), 0),
    soldOutCount: states.filter((s) => s === 'SOLD_OUT').length,
    emptyCount: states.filter((s) => s === 'EMPTY').length,
  };
}

/** 칸의 표시 상태: 재고 없음 → 소진 → 예약 있음 → 가용 순으로 판단한다. */
export function cellState(cell?: StockCell): CellState {
  if (!cell || cell.onHand === 0) return 'EMPTY';
  if (cell.available <= 0) return 'SOLD_OUT';
  return cell.reserved > 0 ? 'RESERVED' : 'AVAILABLE';
}

/** 안 좋은 상태가 우선하는 순서(소진 > 예약 있음 > 가용 > 없음) */
const SEVERITY: CellState[] = ['SOLD_OUT', 'RESERVED', 'AVAILABLE', 'EMPTY'];

/** 위치 코드 목록 중 가장 안 좋은 재고 상태(창고 하나의 요약 신호로 쓴다) */
export function worstCellState(codes: string[], cells: Record<string, StockCell>): CellState {
  const states = codes.map((code) => cellState(cells[code]));
  return SEVERITY.find((s) => states.includes(s)) ?? 'EMPTY';
}
