import { OrderEvent } from '@/types/order';
import { formatDateTime } from '@/utils/format';

/** 이벤트 이력. 감사로그가 연동되면 채워진다. */
function OrderEventList({ events }: { events: OrderEvent[] }) {
  if (events.length === 0) {
    return <p className="text-sm text-gray-400">이벤트 이력이 없습니다.</p>;
  }
  return (
    <ul className="space-y-1 rounded-md border border-gray-200 bg-white p-4 text-sm">
      {events.map((e, i) => (
        <li key={i}>
          <span className="mr-2 text-gray-500">{formatDateTime(e.at)}</span>
          {e.description}
        </li>
      ))}
    </ul>
  );
}

export default OrderEventList;
