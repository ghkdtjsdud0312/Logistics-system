import { BUTTON_PRIMARY, BUTTON_SECONDARY } from '@/constants/styles';
import { addDispatchShipments, cancelDispatch, closeDispatch } from '@/services/dispatchService';

interface LoadingDispatchActionsProps {
  dispatchId: number;
  assignedCount: number;
  hasOrigin: boolean;
  selected: number[];
  overAfterAdd: boolean;
  submitting: boolean;
  handle: (action: () => Promise<unknown>, message: string) => Promise<void>;
  onOptimize: () => void;
}

/** 적재중 배차의 담기·경로 최적화·마감·취소 버튼 */
function LoadingDispatchActions({
  dispatchId,
  assignedCount,
  hasOrigin,
  selected,
  overAfterAdd,
  submitting,
  handle,
  onOptimize,
}: LoadingDispatchActionsProps) {
  return (
    <div className="flex flex-wrap items-center gap-2">
      <button
        className={BUTTON_SECONDARY}
        disabled={submitting || selected.length === 0 || overAfterAdd}
        onClick={() =>
          handle(() => addDispatchShipments(dispatchId, selected), '배송을 담았습니다.')
        }
      >
        선택한 배송 담기 ({selected.length}건)
      </button>
      {overAfterAdd && selected.length > 0 && <span className="text-red-600">적재량 초과</span>}
      <button
        className={BUTTON_SECONDARY}
        disabled={submitting || assignedCount < 2 || !hasOrigin}
        onClick={onOptimize}
      >
        경로 최적화
      </button>
      {hasOrigin && assignedCount < 2 && (
        <span className="text-xs text-gray-400">배송 2건 이상일 때 쓸 수 있습니다.</span>
      )}
      <button
        className={`${BUTTON_PRIMARY} ml-auto`}
        disabled={submitting || assignedCount === 0}
        onClick={() => handle(() => closeDispatch(dispatchId), '적재를 마감했습니다.')}
      >
        적재 마감
      </button>
      <button
        className={BUTTON_SECONDARY}
        disabled={submitting}
        onClick={() => handle(() => cancelDispatch(dispatchId), '배차를 취소했습니다.')}
      >
        취소
      </button>
    </div>
  );
}

export default LoadingDispatchActions;
