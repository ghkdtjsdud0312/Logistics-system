import { useDispatchDetail } from '@/hooks/useDispatchDetail';
import { nextDispatchStatus } from '@/utils/dispatchStatusFlow';
import StopList from './StopList';
import StatusHistoryList from './StatusHistoryList';

interface Props {
  dispatchId: number;
  onChanged: () => void;
}

/** 배차 상세: 경로 최적화, 경유지/상태 이력, 다음 단계 전이 */
function DispatchDetailView({ dispatchId, onChanged }: Props) {
  const { detail, runOptimize, advanceStatus, advanceStop } = useDispatchDetail(dispatchId, onChanged);

  if (!detail) {
    return <p className="text-sm text-gray-500">불러오는 중...</p>;
  }

  const next = nextDispatchStatus(detail.status);

  return (
    <div className="space-y-4 rounded border bg-white p-3 text-sm">
      <div className="flex items-center gap-2">
        <button type="button" onClick={runOptimize} className="rounded bg-gray-700 px-3 py-1 text-xs text-white">
          경로 최적화
        </button>
        {next && (
          <button type="button" onClick={advanceStatus} className="rounded bg-gray-900 px-3 py-1 text-xs text-white">
            {next}(으)로 변경
          </button>
        )}
      </div>

      <div>
        <h4 className="mb-1 text-xs font-semibold text-gray-700">경로</h4>
        <StopList stops={detail.stops} onAdvance={advanceStop} />
      </div>

      <div>
        <h4 className="mb-1 text-xs font-semibold text-gray-700">상태 이력</h4>
        <StatusHistoryList history={detail.statusHistory} />
      </div>
    </div>
  );
}

export default DispatchDetailView;
