import { ORDER_STATUS_LABEL } from '@/constants/orderStatus';
import { TimelineStep } from '@/types/order';
import { formatDateTime } from '@/utils/format';

/** 물류 진행 상황: ● 완료 / ○ 미완료 */
function OrderTimeline({ steps }: { steps: TimelineStep[] }) {
  return (
    <ol className="space-y-1 rounded-md border border-gray-200 bg-white p-4 text-sm">
      {steps.map((s) => (
        <li key={s.step} className={s.done ? 'text-gray-900' : 'text-gray-400'}>
          {s.done ? '●' : '○'} {ORDER_STATUS_LABEL[s.step]}
          {s.at && <span className="ml-2 text-xs text-gray-500">{formatDateTime(s.at)}</span>}
        </li>
      ))}
    </ol>
  );
}

export default OrderTimeline;
