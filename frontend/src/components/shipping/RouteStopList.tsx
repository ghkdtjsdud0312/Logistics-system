import { BUTTON_SECONDARY } from '@/constants/styles';
import { Shipment } from '@/types/shipment';
import { loadingSequence } from '@/utils/route';

interface RouteStopListProps {
  /** 방문 순서대로 정렬된 배송 */
  stops: Shipment[];
  disabled: boolean;
  onMove: (index: number, offset: number) => void;
  onRemove: (shipmentId: number) => void;
}

/** 방문 순서 목록(↑↓로 조정)과, 그 역순인 적재 순서 안내 */
function RouteStopList({ stops, disabled, onMove, onRemove }: RouteStopListProps) {
  if (stops.length === 0) {
    return <p className="py-1 text-gray-400">담긴 배송이 없습니다.</p>;
  }
  const sequence = loadingSequence(stops);
  return (
    <div className="space-y-2">
      <ol className="divide-y divide-gray-100">
        {stops.map((s, i) => (
          <li key={s.id} className="flex items-center gap-3 py-1">
            <span className="w-10 text-xs text-gray-400">방문 {i + 1}</span>
            <span className="font-medium">{s.orderNo}</span>
            <span className="text-gray-500">
              {s.address} · {s.totalWeightKg}kg
            </span>
            <div className="ml-auto flex gap-1">
              <button
                className={BUTTON_SECONDARY}
                disabled={disabled || i === 0}
                onClick={() => onMove(i, -1)}
              >
                ↑
              </button>
              <button
                className={BUTTON_SECONDARY}
                disabled={disabled || i === stops.length - 1}
                onClick={() => onMove(i, 1)}
              >
                ↓
              </button>
              <button
                className={BUTTON_SECONDARY}
                disabled={disabled}
                onClick={() => onRemove(s.id)}
              >
                제외
              </button>
            </div>
          </li>
        ))}
      </ol>
      <p className="rounded bg-gray-50 px-2 py-1 text-xs text-gray-600">
        적재 순서(먼저 싣기 → 마지막에 싣기): {sequence.map((s) => s.orderNo).join(' → ')}
      </p>
    </div>
  );
}

export default RouteStopList;
