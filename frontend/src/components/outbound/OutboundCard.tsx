import { Outbound } from '@/types/outbound';
import { formatDate } from '@/utils/formatDate';
import { pickOutbound, shipOutbound } from '@/services/outboundService';

interface Props {
  outbound: Outbound;
  onChanged: () => void;
}

/** 칸반 카드 하나 - 도착지/물량 정보 + 현재 상태에 맞는 다음 단계 액션 */
function OutboundCard({ outbound, onChanged }: Props) {
  return (
    <div className="rounded-lg border bg-white p-3 shadow-sm">
      <p className="font-medium text-gray-900">{outbound.destination}</p>
      <p className="mt-1 text-xs text-gray-500">
        {outbound.items.length}건 · 총 {outbound.totalQuantity}개 · {outbound.totalWeightKg}kg · {outbound.totalVolumeM3}㎥
      </p>
      <p className="text-xs text-gray-400">{formatDate(outbound.createdAt)}</p>

      {outbound.status === 'REQUESTED' && (
        <button
          className="mt-2 w-full rounded bg-gray-900 py-1 text-xs text-white"
          onClick={() => pickOutbound(outbound.id).then(onChanged)}
        >
          피킹 시작
        </button>
      )}

      {outbound.status === 'PICKING' && (
        <button
          className="mt-2 w-full rounded bg-gray-900 py-1 text-xs text-white"
          onClick={() => shipOutbound(outbound.id).then(onChanged)}
        >
          출고 완료
        </button>
      )}

      {outbound.status === 'SHIPPED' && (
        <p className="mt-2 text-xs font-medium text-green-600">✓ 출고 완료</p>
      )}
    </div>
  );
}

export default OutboundCard;
