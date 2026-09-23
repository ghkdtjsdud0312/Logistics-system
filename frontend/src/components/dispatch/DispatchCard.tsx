import { useState } from 'react';
import { Dispatch } from '@/types/dispatch';
import { DISPATCH_STATUS_LABEL } from '@/constants/dispatchStatus';
import DispatchDetailView from './DispatchDetailView';

interface Props {
  dispatch: Dispatch;
  onChanged: () => void;
}

/** 배차 카드: 차량/기사/물량 요약 + 펼치면 경로/상태 상세(DispatchDetailView) */
function DispatchCard({ dispatch, onChanged }: Props) {
  const [expanded, setExpanded] = useState(false);

  return (
    <div className="rounded-lg border bg-white p-3 shadow-sm">
      <p className="font-medium text-gray-900">배차 #{dispatch.id}</p>
      <p className="mt-1 text-xs text-gray-500">
        차량 #{dispatch.vehicleId} · 기사 #{dispatch.driverId} · 출고 {dispatch.outboundIds.length}건
      </p>
      <p className="text-xs text-gray-400">
        {dispatch.totalWeightKg}kg · {dispatch.totalVolumeM3}㎥ · {new Date(dispatch.plannedAt).toLocaleString()}
      </p>
      <p className="mt-1 text-xs font-medium text-gray-600">{DISPATCH_STATUS_LABEL[dispatch.status]}</p>

      <button
        type="button"
        className="mt-2 w-full rounded border border-gray-300 py-1 text-xs text-gray-700"
        onClick={() => setExpanded((prev) => !prev)}
      >
        {expanded ? '상세 닫기' : '상세 보기'}
      </button>

      {expanded && (
        <div className="mt-2">
          <DispatchDetailView dispatchId={dispatch.id} onChanged={onChanged} />
        </div>
      )}
    </div>
  );
}

export default DispatchCard;
