import { useState } from 'react';
import { BUTTON_PRIMARY } from '@/constants/styles';
import { INPUT_CLASS } from '@/constants/styles';
import { useSubmit } from '@/hooks/useSubmit';
import { completePicking, startPicking } from '@/services/workService';
import { PickingTask } from '@/types/work';

/** 대기 → 시작, 진행중 → 수량 입력 후 피킹완료 */
function PickingActions({ task, onChanged }: { task: PickingTask; onChanged: () => void }) {
  const [qty, setQty] = useState(String(task.requestedQty));
  const { submitting, run } = useSubmit();
  const handle = async (action: () => Promise<unknown>, message: string) => {
    if (await run(action, message)) onChanged();
  };

  if (task.status === 'WAITING') {
    return (
      <button
        className={BUTTON_PRIMARY}
        disabled={submitting}
        onClick={() => handle(() => startPicking(task.id), '피킹을 시작했습니다.')}
      >
        피킹시작
      </button>
    );
  }
  if (task.status === 'IN_PROGRESS') {
    return (
      <div className="flex items-center gap-2">
        <input
          className={`${INPUT_CLASS} w-20`}
          type="number"
          value={qty}
          onChange={(e) => setQty(e.target.value)}
        />
        <button
          className={BUTTON_PRIMARY}
          disabled={submitting}
          onClick={() =>
            handle(() => completePicking(task.id, Number(qty)), '피킹을 완료했습니다.')
          }
        >
          피킹완료
        </button>
      </div>
    );
  }
  return <span className="text-gray-400">-</span>;
}

export default PickingActions;
