import { ORDER_STATUS_LABEL } from '@/constants/orderStatus';
import { TimelineStep } from '@/types/order';
import { formatDateTime } from '@/utils/format';

/** 물류 진행 상황을 가로 단계로 보여 준다: ●─●─○ (완료는 파랑, 미완료는 회색) */
function OrderTimeline({ steps }: { steps: TimelineStep[] }) {
  return (
    <ol className="flex overflow-x-auto rounded-md border border-gray-200 bg-white p-4">
      {steps.map((s, i) => (
        <li key={s.step} className="flex min-w-24 flex-1 flex-col items-center text-center">
          <div className="flex w-full items-center">
            <span
              className={`h-0.5 flex-1 ${i === 0 ? 'bg-transparent' : s.done ? 'bg-primary' : 'bg-gray-200'}`}
            />
            <span
              className={`flex h-6 w-6 items-center justify-center rounded-full text-xs ${
                s.done
                  ? 'bg-primary text-white'
                  : 'border-2 border-gray-300 bg-white text-transparent'
              }`}
            >
              ✓
            </span>
            <span
              className={`h-0.5 flex-1 ${
                i === steps.length - 1
                  ? 'bg-transparent'
                  : steps[i + 1].done
                    ? 'bg-primary'
                    : 'bg-gray-200'
              }`}
            />
          </div>
          <span
            className={`mt-2 text-xs font-medium ${s.done ? 'text-gray-900' : 'text-gray-400'}`}
          >
            {ORDER_STATUS_LABEL[s.step]}
          </span>
          <span className="text-xs text-gray-500">{s.at ? formatDateTime(s.at) : '\u00a0'}</span>
        </li>
      ))}
    </ol>
  );
}

export default OrderTimeline;
