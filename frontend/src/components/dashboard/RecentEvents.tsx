import { DashboardEvent } from '@/types/dashboard';
import { formatDateTime } from '@/utils/format';

/** 최근 물류 이벤트 */
function RecentEvents({ events }: { events: DashboardEvent[] }) {
  if (events.length === 0) {
    return <p className="text-sm text-gray-400">최근 이벤트가 없습니다.</p>;
  }
  return (
    <ul className="space-y-1 rounded-md border border-gray-200 bg-white p-4 text-sm">
      {events.map((e) => (
        <li key={`${e.at}-${e.description}`}>
          <span className="mr-2 text-gray-500">{formatDateTime(e.at)}</span>
          {e.description}
        </li>
      ))}
    </ul>
  );
}

export default RecentEvents;
