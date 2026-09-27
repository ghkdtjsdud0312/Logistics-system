import { WarehouseSummary } from '@/types/warehouse3d';

/** 창고 전체 요약: 칸 수 대비 상태별 비율 */
function WarehouseSummaryPanel({ summary }: { summary: WarehouseSummary }) {
  const rate =
    summary.totalCells === 0
      ? 0
      : Math.round(((summary.totalCells - summary.emptyCells) / summary.totalCells) * 100);
  return (
    <div className="flex flex-wrap gap-4 rounded-md border border-gray-200 bg-white px-3 py-2 text-xs text-gray-600">
      <span>
        재고 있는 칸 <b className="text-gray-900">{rate}%</b>
      </span>
      <span>전체 {summary.totalCells}칸</span>
      <span>가용 {summary.availableCells}칸</span>
      <span>예약 있음 {summary.reservedCells}칸</span>
      <span className={summary.soldOutCells > 0 ? 'font-semibold text-red-600' : ''}>
        소진 {summary.soldOutCells}칸
      </span>
      <span>빈 칸 {summary.emptyCells}칸</span>
    </div>
  );
}

export default WarehouseSummaryPanel;
