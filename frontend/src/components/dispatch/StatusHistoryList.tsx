import { DispatchStatusHistoryEntry } from '@/types/dispatch';

interface Props {
  history: DispatchStatusHistoryEntry[];
}

/** 배차 상태 변경 이력 */
function StatusHistoryList({ history }: Props) {
  if (history.length === 0) {
    return <p className="text-sm text-gray-500">상태 변경 이력이 없습니다.</p>;
  }

  return (
    <ul className="space-y-1 text-xs text-gray-600">
      {history.map((entry, index) => (
        <li key={index}>
          {new Date(entry.changedAt).toLocaleString()} · {entry.fromStatus} → {entry.toStatus} · {entry.actor}
          {entry.description ? ` · ${entry.description}` : ''}
        </li>
      ))}
    </ul>
  );
}

export default StatusHistoryList;
