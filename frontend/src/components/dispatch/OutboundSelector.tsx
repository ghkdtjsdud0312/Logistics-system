import { Outbound } from '@/types/outbound';

interface Props {
  outbounds: Outbound[];
  selectedIds: number[];
  plannedAt: string;
  onToggle: (id: number) => void;
  onPlannedAtChange: (value: string) => void;
}

/** 배차 대상 출고 계획 선택 + 배차 계획 시각 입력 */
function OutboundSelector({ outbounds, selectedIds, plannedAt, onToggle, onPlannedAtChange }: Props) {
  return (
    <div className="space-y-3">
      <div>
        <label className="block text-xs text-gray-500">배차 계획 시각</label>
        <input
          type="datetime-local"
          className="rounded border px-2 py-1"
          value={plannedAt}
          onChange={(e) => onPlannedAtChange(e.target.value)}
          required
        />
      </div>

      {outbounds.length === 0 ? (
        <p className="text-sm text-gray-500">배차 대상 출고 계획이 없습니다.</p>
      ) : (
        <ul className="max-h-56 space-y-1 overflow-y-auto rounded border p-2">
          {outbounds.map((outbound) => (
            <li key={outbound.id}>
              <label className="flex items-center gap-2 text-sm">
                <input
                  type="checkbox"
                  checked={selectedIds.includes(outbound.id)}
                  onChange={() => onToggle(outbound.id)}
                />
                <span>
                  #{outbound.id} {outbound.destination} · {outbound.totalWeightKg}kg · {outbound.totalVolumeM3}㎥
                </span>
              </label>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}

export default OutboundSelector;
