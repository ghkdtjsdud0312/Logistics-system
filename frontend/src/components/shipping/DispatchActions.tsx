import { BUTTON_PRIMARY, BUTTON_SECONDARY } from '@/constants/styles';
import { useSubmit } from '@/hooks/useSubmit';
import { cancelDispatch, startDispatch } from '@/services/dispatchService';
import { Dispatch } from '@/types/dispatch';

/** 출발대기 상태에서만 배송 시작·취소 가능 (적재중 배차는 적재 카드에서 처리) */
function DispatchActions({ dispatch, onChanged }: { dispatch: Dispatch; onChanged: () => void }) {
  const { submitting, run } = useSubmit();
  if (dispatch.status !== 'REGISTERED') return <span className="text-gray-400">-</span>;

  const handle = async (action: () => Promise<unknown>, message: string) => {
    if (await run(action, message)) onChanged();
  };
  return (
    <div className="flex gap-2">
      <button
        className={BUTTON_PRIMARY}
        disabled={submitting}
        onClick={() => handle(() => startDispatch(dispatch.id), '배송을 시작했습니다.')}
      >
        배송시작
      </button>
      <button
        className={BUTTON_SECONDARY}
        disabled={submitting}
        onClick={() => handle(() => cancelDispatch(dispatch.id), '배차를 취소했습니다.')}
      >
        취소
      </button>
    </div>
  );
}

export default DispatchActions;
