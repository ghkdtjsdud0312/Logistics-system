import { Dispatch, DispatchStatus } from '@/types/dispatch';
import { DISPATCH_STATUS_LABEL } from '@/constants/dispatchStatus';
import DispatchCard from './DispatchCard';

interface Props {
  dispatches: Dispatch[];
  onChanged: () => void;
}

const COLUMNS: DispatchStatus[] = ['CONFIRMED', 'LOADED', 'IN_TRANSIT', 'COMPLETED'];

/** 배차 칸반 보드: 상태별 컬럼에 카드를 배치한다 */
function DispatchBoard({ dispatches, onChanged }: Props) {
  return (
    <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-4">
      {COLUMNS.map((status) => {
        const items = dispatches.filter((d) => d.status === status);
        return (
          <div key={status} className="rounded-lg bg-gray-50 p-3">
            <h3 className="mb-2 text-sm font-semibold text-gray-700">
              {DISPATCH_STATUS_LABEL[status]} <span className="text-gray-400">({items.length})</span>
            </h3>
            <div className="space-y-2">
              {items.map((dispatch) => (
                <DispatchCard key={dispatch.id} dispatch={dispatch} onChanged={onChanged} />
              ))}
            </div>
          </div>
        );
      })}
    </div>
  );
}

export default DispatchBoard;
