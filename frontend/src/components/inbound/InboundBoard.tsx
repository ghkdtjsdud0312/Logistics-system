import { Inbound, InboundStatus } from '@/types/inbound';
import InboundCard from './InboundCard';

interface Props {
  inbounds: Inbound[];
  onChanged: () => void;
}

const COLUMNS: { status: InboundStatus; title: string }[] = [
  { status: 'REQUESTED', title: '입고 요청' },
  { status: 'IN_PROGRESS', title: '입고 처리중' },
  { status: 'COMPLETED', title: '검수 완료' },
];

/** 입고 칸반 보드: 상태별 컬럼에 카드를 배치해 진행 상황을 한눈에 보여준다 */
function InboundBoard({ inbounds, onChanged }: Props) {
  return (
    <div className="grid grid-cols-1 gap-4 sm:grid-cols-3">
      {COLUMNS.map((column) => {
        const items = inbounds.filter((inbound) => inbound.status === column.status);
        return (
          <div key={column.status} className="rounded-lg bg-gray-50 p-3">
            <h3 className="mb-2 text-sm font-semibold text-gray-700">
              {column.title} <span className="text-gray-400">({items.length})</span>
            </h3>
            <div className="space-y-2">
              {items.map((inbound) => (
                <InboundCard key={inbound.id} inbound={inbound} onChanged={onChanged} />
              ))}
            </div>
          </div>
        );
      })}
    </div>
  );
}

export default InboundBoard;
