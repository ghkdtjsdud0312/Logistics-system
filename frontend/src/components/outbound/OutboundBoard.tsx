import { Outbound, OutboundStatus } from '@/types/outbound';
import OutboundCard from './OutboundCard';

interface Props {
  outbounds: Outbound[];
  onChanged: () => void;
}

const COLUMNS: { status: OutboundStatus; title: string }[] = [
  { status: 'REQUESTED', title: '출고 계획' },
  { status: 'PICKING', title: '피킹중' },
  { status: 'SHIPPED', title: '출고 완료' },
];

/** 출고 칸반 보드: 상태별 컬럼에 카드를 배치해 진행 상황을 한눈에 보여준다 */
function OutboundBoard({ outbounds, onChanged }: Props) {
  return (
    <div className="grid grid-cols-1 gap-4 sm:grid-cols-3">
      {COLUMNS.map((column) => {
        const items = outbounds.filter((outbound) => outbound.status === column.status);
        return (
          <div key={column.status} className="rounded-lg bg-gray-50 p-3">
            <h3 className="mb-2 text-sm font-semibold text-gray-700">
              {column.title} <span className="text-gray-400">({items.length})</span>
            </h3>
            <div className="space-y-2">
              {items.map((outbound) => (
                <OutboundCard key={outbound.id} outbound={outbound} onChanged={onChanged} />
              ))}
            </div>
          </div>
        );
      })}
    </div>
  );
}

export default OutboundBoard;
