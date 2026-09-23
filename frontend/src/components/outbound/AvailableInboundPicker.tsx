import { AvailableInbound, SelectedOutboundLine } from '@/types/outbound';

interface Props {
  availableInbounds: AvailableInbound[];
  selected: Record<number, SelectedOutboundLine>;
  onChange: (inboundId: number, line: Partial<SelectedOutboundLine>) => void;
}

/** 출고 대상 선정: 검수 완료된 가용 입고 목록에서 수량·중량·부피를 지정한다 */
function AvailableInboundPicker({ availableInbounds, selected, onChange }: Props) {
  if (availableInbounds.length === 0) {
    return <p className="text-sm text-gray-500">출고 가능한 입고 물량이 없습니다.</p>;
  }

  return (
    <table className="w-full text-left text-sm">
      <thead>
        <tr className="border-b bg-gray-50">
          <th className="px-3 py-2">품목명</th>
          <th className="px-3 py-2">창고 위치</th>
          <th className="px-3 py-2">가용 수량</th>
          <th className="px-3 py-2">출고 수량</th>
          <th className="px-3 py-2">중량(kg)</th>
          <th className="px-3 py-2">부피(㎥)</th>
        </tr>
      </thead>
      <tbody>
        {availableInbounds.map((item) => {
          const line = selected[item.inboundId];
          return (
            <tr key={item.inboundId} className="border-b">
              <td className="px-3 py-2">{item.itemName}</td>
              <td className="px-3 py-2">{item.warehouseLocation}</td>
              <td className="px-3 py-2">{item.availableQuantity}</td>
              <td className="px-3 py-2">
                <input
                  type="number"
                  min={0}
                  max={item.availableQuantity}
                  className="w-20 rounded border px-1 py-0.5"
                  value={line?.quantity || ''}
                  onChange={(e) => onChange(item.inboundId, { quantity: Number(e.target.value) || 0 })}
                />
              </td>
              <td className="px-3 py-2">
                <input
                  type="number"
                  min={0}
                  step="0.1"
                  className="w-20 rounded border px-1 py-0.5"
                  value={line?.weightKg || ''}
                  onChange={(e) => onChange(item.inboundId, { weightKg: Number(e.target.value) || 0 })}
                />
              </td>
              <td className="px-3 py-2">
                <input
                  type="number"
                  min={0}
                  step="0.01"
                  className="w-20 rounded border px-1 py-0.5"
                  value={line?.volumeM3 || ''}
                  onChange={(e) => onChange(item.inboundId, { volumeM3: Number(e.target.value) || 0 })}
                />
              </td>
            </tr>
          );
        })}
      </tbody>
    </table>
  );
}

export default AvailableInboundPicker;
