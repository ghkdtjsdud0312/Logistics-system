import { WarehouseSummary } from '@/utils/warehouseSummary';

interface Props {
  summaries: WarehouseSummary[];
}

/** 창고 위치별 가용 재고를 카드로 요약 - 출고 가능한 물량만 집계한다 */
function WarehouseSummaryBoard({ summaries }: Props) {
  if (summaries.length === 0) {
    return <p className="text-sm text-gray-500">현재 창고에 출고 가능한 재고가 없습니다.</p>;
  }

  return (
    <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-4">
      {summaries.map((summary) => (
        <div key={summary.warehouseLocation} className="rounded-lg border bg-white p-4 shadow-sm">
          <p className="text-sm font-semibold text-gray-900">{summary.warehouseLocation}</p>
          <p className="mt-1 text-2xl font-bold text-gray-900">{summary.totalQuantity}개</p>
          <ul className="mt-2 space-y-0.5 text-xs text-gray-500">
            {summary.items.map((item) => (
              <li key={item.inboundId}>
                {item.itemName} · {item.availableQuantity}개
              </li>
            ))}
          </ul>
        </div>
      ))}
    </div>
  );
}

export default WarehouseSummaryBoard;
