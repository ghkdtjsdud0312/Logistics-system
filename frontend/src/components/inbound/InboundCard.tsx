import { useState } from 'react';
import { Inbound } from '@/types/inbound';
import { formatDate } from '@/utils/formatDate';
import { completeInbound, startInbound } from '@/services/inboundService';

interface Props {
  inbound: Inbound;
  onChanged: () => void;
}

/** 칸반 카드 하나 - 품목 정보 + 현재 상태에 맞는 다음 단계 액션 */
function InboundCard({ inbound, onChanged }: Props) {
  const [inspectedQuantity, setInspectedQuantity] = useState(inbound.quantity);

  return (
    <div className="rounded-lg border bg-white p-3 shadow-sm">
      <p className="font-medium text-gray-900">{inbound.itemName}</p>
      <p className="mt-1 text-xs text-gray-500">
        {inbound.quantity}개 · {inbound.warehouseLocation}
      </p>
      <p className="text-xs text-gray-400">{formatDate(inbound.createdAt)}</p>

      {inbound.status === 'REQUESTED' && (
        <button
          className="mt-2 w-full rounded bg-gray-900 py-1 text-xs text-white"
          onClick={() => startInbound(inbound.id).then(onChanged)}
        >
          입고 처리 시작
        </button>
      )}

      {inbound.status === 'IN_PROGRESS' && (
        <div className="mt-2 flex items-center gap-1">
          <input
            type="number"
            min={0}
            className="w-16 rounded border px-1 py-0.5 text-xs"
            value={inspectedQuantity || ''}
            onChange={(e) => {
              const raw = e.target.value;
              setInspectedQuantity(raw === '' ? 0 : Number(raw));
            }}
          />
          <button
            className="flex-1 rounded bg-gray-900 py-1 text-xs text-white"
            onClick={() => completeInbound(inbound.id, { inspectedQuantity }).then(onChanged)}
          >
            검수 완료
          </button>
        </div>
      )}

      {inbound.status === 'COMPLETED' && (
        <p className="mt-2 text-xs font-medium text-green-600">
          ✓ 검수 {inbound.inspectedQuantity}개
        </p>
      )}
    </div>
  );
}

export default InboundCard;
