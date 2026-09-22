import { FormEvent, useEffect, useState } from 'react';
import { toast } from 'sonner';
import { AvailableInbound } from '@/types/outbound';
import { createOutbound, getAvailableInbounds } from '@/services/outboundService';
import AvailableInboundPicker from './AvailableInboundPicker';

interface Props {
  onCreated: () => void;
}

/** 출고 계획 생성 폼: 도착지 + 여러 입고건 조합 (1:N) */
function OutboundForm({ onCreated }: Props) {
  const [destination, setDestination] = useState('');
  const [available, setAvailable] = useState<AvailableInbound[]>([]);
  const [selected, setSelected] = useState<Record<number, number>>({});

  const reloadAvailable = () =>
    getAvailableInbounds()
      .then(setAvailable)
      .catch(() => toast.error('출고 대상 목록을 불러오지 못했습니다.'));

  useEffect(() => {
    reloadAvailable();
  }, []);

  const handleChange = (inboundId: number, quantity: number) => {
    setSelected((prev) => ({ ...prev, [inboundId]: quantity }));
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    const items = Object.entries(selected)
      .map(([inboundId, quantity]) => ({ inboundId: Number(inboundId), quantity }))
      .filter((item) => item.quantity > 0);

    if (items.length === 0) {
      toast.error('출고할 물량을 1건 이상 선택하세요.');
      return;
    }

    try {
      await createOutbound({ destination, items });
      setDestination('');
      setSelected({});
      onCreated();
      reloadAvailable();
    } catch {
      toast.error('출고 계획 생성에 실패했습니다. 가용 수량을 확인하세요.');
    }
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-3 rounded border p-4">
      <div>
        <label className="block text-xs text-gray-500">도착지</label>
        <input
          className="rounded border px-2 py-1"
          value={destination}
          onChange={(e) => setDestination(e.target.value)}
          required
        />
      </div>
      <AvailableInboundPicker
        availableInbounds={available}
        selected={selected}
        onChange={handleChange}
      />
      <button type="submit" className="rounded bg-gray-900 px-3 py-1.5 text-white">
        출고 계획 생성
      </button>
    </form>
  );
}

export default OutboundForm;
