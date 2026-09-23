import { DispatchCandidateResult } from '@/types/dispatch';

interface Props {
  result: DispatchCandidateResult;
  selectedVehicleId: number | null;
  onSelect: (vehicleId: number) => void;
}

const toPercent = (ratio: number) => `${Math.round(ratio * 100)}%`;

/** 차량 후보 표: 추천 차량은 선택 가능, 제외 차량은 사유만 보여준다 */
function VehicleCandidateTable({ result, selectedVehicleId, onSelect }: Props) {
  return (
    <div className="space-y-3 text-sm">
      <p className="text-gray-600">
        합산 적재량: {result.totalWeightKg}kg · {result.totalVolumeM3}㎥
      </p>

      <table className="w-full text-left">
        <thead>
          <tr className="border-b bg-gray-50">
            <th className="px-2 py-1">차량번호</th>
            <th className="px-2 py-1">중량 사용률</th>
            <th className="px-2 py-1">부피 사용률</th>
            <th className="px-2 py-1">점수</th>
            <th className="px-2 py-1" />
          </tr>
        </thead>
        <tbody>
          {result.candidates.map((c) => (
            <tr key={c.vehicleId} className="border-b">
              <td className="px-2 py-1">{c.vehicleNumber}</td>
              <td className="px-2 py-1">{toPercent(c.usedWeightRatio)}</td>
              <td className="px-2 py-1">{toPercent(c.usedVolumeRatio)}</td>
              <td className="px-2 py-1">{c.score.toFixed(2)}</td>
              <td className="px-2 py-1">
                <button
                  type="button"
                  className={`rounded px-2 py-0.5 text-xs text-white ${
                    selectedVehicleId === c.vehicleId ? 'bg-primary' : 'bg-gray-900'
                  }`}
                  onClick={() => onSelect(c.vehicleId)}
                >
                  {selectedVehicleId === c.vehicleId ? '선택됨' : '선택'}
                </button>
              </td>
            </tr>
          ))}
          {result.candidates.length === 0 && (
            <tr>
              <td colSpan={5} className="px-2 py-2 text-gray-500">
                추천 가능한 차량이 없습니다.
              </td>
            </tr>
          )}
        </tbody>
      </table>

      {result.excluded.length > 0 && (
        <ul className="space-y-1 text-xs text-gray-500">
          {result.excluded.map((e) => (
            <li key={e.vehicleId}>
              {e.vehicleNumber} 제외: {e.reasonMessage}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}

export default VehicleCandidateTable;
