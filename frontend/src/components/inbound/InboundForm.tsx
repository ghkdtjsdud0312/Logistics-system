import { FormEvent, useState } from 'react';
import { createInbound } from '@/services/inboundService';

interface Props {
  onCreated: () => void;
}

/** 입고 요청 등록 폼 */
function InboundForm({ onCreated }: Props) {
  const [itemName, setItemName] = useState('');
  const [quantity, setQuantity] = useState(1);
  const [warehouseLocation, setWarehouseLocation] = useState('');

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    await createInbound({ itemName, quantity, warehouseLocation });
    setItemName('');
    setQuantity(1);
    setWarehouseLocation('');
    onCreated();
  };

  return (
    <form onSubmit={handleSubmit} className="flex flex-wrap items-end gap-2 rounded border p-4">
      <div>
        <label className="block text-xs text-gray-500">품목명</label>
        <input
          className="rounded border px-2 py-1"
          value={itemName}
          onChange={(e) => setItemName(e.target.value)}
          required
        />
      </div>
      <div>
        <label className="block text-xs text-gray-500">수량</label>
        <input
          type="number"
          min={1}
          className="w-24 rounded border px-2 py-1"
          value={quantity || ''}
          onChange={(e) => {
            const raw = e.target.value;
            setQuantity(raw === '' ? 0 : Number(raw));
          }}
          required
        />
      </div>
      <div>
        <label className="block text-xs text-gray-500">창고 위치</label>
        <input
          className="rounded border px-2 py-1"
          value={warehouseLocation}
          onChange={(e) => setWarehouseLocation(e.target.value)}
          required
        />
      </div>
      <button type="submit" className="rounded bg-gray-900 px-3 py-1.5 text-white">
        입고 요청
      </button>
    </form>
  );
}

export default InboundForm;
