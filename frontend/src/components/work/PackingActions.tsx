import { useState } from 'react';
import { BUTTON_PRIMARY, INPUT_CLASS } from '@/constants/styles';
import { useSubmit } from '@/hooks/useSubmit';
import { completePacking } from '@/services/workService';
import { PackingTask } from '@/types/work';

/** 박스 코드를 입력해 포장 완료 */
function PackingActions({ task, onChanged }: { task: PackingTask; onChanged: () => void }) {
  const [boxCode, setBoxCode] = useState('');
  const { submitting, run } = useSubmit();

  if (task.status === 'COMPLETED') {
    return <span className="text-gray-400">{task.boxCode}</span>;
  }
  return (
    <div className="flex items-center gap-2">
      <input
        className={`${INPUT_CLASS} w-28`}
        placeholder="BOX-001"
        value={boxCode}
        onChange={(e) => setBoxCode(e.target.value)}
      />
      <button
        className={BUTTON_PRIMARY}
        disabled={submitting || !boxCode}
        onClick={async () => {
          if (await run(() => completePacking(task.id, boxCode), '포장을 완료했습니다.'))
            onChanged();
        }}
      >
        포장완료
      </button>
    </div>
  );
}

export default PackingActions;
