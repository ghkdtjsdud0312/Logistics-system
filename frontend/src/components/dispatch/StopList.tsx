import { RouteStop } from '@/types/dispatch';
import { nextStopStatus } from '@/utils/dispatchStatusFlow';

interface Props {
  stops: RouteStop[];
  onAdvance: (stopId: number, current: RouteStop['status']) => void;
}

/** 경로 경유지 목록: 순서/거리/상태와 다음 단계 버튼 */
function StopList({ stops, onAdvance }: Props) {
  if (stops.length === 0) {
    return <p className="text-sm text-gray-500">아직 경로가 계산되지 않았습니다.</p>;
  }

  return (
    <table className="w-full text-left text-sm">
      <thead>
        <tr className="border-b bg-gray-50">
          <th className="px-2 py-1">순서</th>
          <th className="px-2 py-1">경유지</th>
          <th className="px-2 py-1">이전 지점 거리</th>
          <th className="px-2 py-1">상태</th>
          <th className="px-2 py-1" />
        </tr>
      </thead>
      <tbody>
        {stops.map((stop) => {
          const next = nextStopStatus(stop.status);
          return (
            <tr key={stop.id} className="border-b">
              <td className="px-2 py-1">{stop.sequence}</td>
              <td className="px-2 py-1">{stop.label}</td>
              <td className="px-2 py-1">{stop.distanceFromPreviousKm.toFixed(1)}km</td>
              <td className="px-2 py-1">{stop.status}</td>
              <td className="px-2 py-1">
                {next && (
                  <button
                    type="button"
                    className="rounded bg-gray-700 px-2 py-0.5 text-xs text-white"
                    onClick={() => onAdvance(stop.id, stop.status)}
                  >
                    {next}로 변경
                  </button>
                )}
              </td>
            </tr>
          );
        })}
      </tbody>
    </table>
  );
}

export default StopList;
