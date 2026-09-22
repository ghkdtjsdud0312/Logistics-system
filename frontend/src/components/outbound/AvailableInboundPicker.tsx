import { AvailableInbound } from '@/types/outbound';

interface Props {
  availableInbounds: AvailableInbound[];
  selected: Record<number, number>;
  onChange: (inboundId: number, quantity: number) => void;
}

/** 출고 대상 선정: 검수 완료된 가용 입고 목록에서 수량을 지정한다 */
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
        </tr>
      </thead>
      <tbody>
        {availableInbounds.map((item) => (
          <tr key={item.inboundId} className="border-b">
            <td className="px-3 py-2">{item.itemName}</td>
            <td className="px-3 py-2">{item.warehouseLocation}</td>
            <td className="px-3 py-2">{item.availableQuantity}</td>
            <td className="px-3 py-2">
              <input
                type="number"
                min={0}
                max={item.availableQuantity}
                className="w-24 rounded border px-1 py-0.5"
                value={selected[item.inboundId] || ''}
                onChange={(e) => {
                  const raw = e.target.value;
                  onChange(item.inboundId, raw === '' ? 0 : Number(raw));
                }}
              />
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}

export default AvailableInboundPicker;
