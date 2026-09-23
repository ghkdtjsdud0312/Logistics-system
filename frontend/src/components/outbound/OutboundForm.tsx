import { FormEvent, useEffect, useState } from 'react';
import { toast } from 'sonner';
import { AvailableInbound, SelectedOutboundLine } from '@/types/outbound';
import { createOutbound, getAvailableInbounds } from '@/services/outboundService';
import AvailableInboundPicker from './AvailableInboundPicker';

interface Props {
  onCreated: () => void;
}

/** 출고 계획 생성 폼: 도착지 + 여러 입고건 조합(1:N) + 품목별 중량·부피 */
function OutboundForm({ onCreated }: Props) {
  const [destination, setDestination] = useState('');
  const [latitude, setLatitude] = useState('');
  const [longitude, setLongitude] = useState('');
  const [available, setAvailable] = useState<AvailableInbound[]>([]);
  const [selected, setSelected] = useState<Record<number, SelectedOutboundLine>>({});

  const reloadAvailable = () =>
    getAvailableInbounds()
      .then(setAvailable)
      .catch(() => toast.error('출고 대상 목록을 불러오지 못했습니다.'));

  useEffect(() => {
    reloadAvailable();
  }, []);

  const handleChange = (inboundId: number, line: Partial<SelectedOutboundLine>) => {
    setSelected((prev) => {
      const current = prev[inboundId] ?? { quantity: 0, weightKg: 0, volumeM3: 0 };
      return { ...prev, [inboundId]: { ...current, ...line } };
    });
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    const items = Object.entries(selected)
      .map(([inboundId, line]) => ({ inboundId: Number(inboundId), ...line }))
      .filter((item) => item.quantity > 0);

    if (items.length === 0) {
      toast.error('출고할 물량을 1건 이상 선택하세요.');
      return;
    }
    if (items.some((item) => item.weightKg <= 0 || item.volumeM3 <= 0)) {
      toast.error('선택한 품목은 중량과 부피를 0보다 크게 입력하세요.');
      return;
    }
    if (!latitude || !longitude) {
      toast.error('배송지 좌표(위도/경도)를 입력하세요.');
      return;
    }

    try {
      await createOutbound({ destination, latitude: Number(latitude), longitude: Number(longitude), items });
      setDestination('');
      setLatitude('');
      setLongitude('');
      setSelected({});
      onCreated();
      reloadAvailable();
    } catch {
      toast.error('출고 계획 생성에 실패했습니다. 가용 수량을 확인하세요.');
    }
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-3 rounded border p-4">
      <div className="flex flex-wrap gap-2">
        <div>
          <label className="block text-xs text-gray-500">도착지</label>
          <input
            className="rounded border px-2 py-1"
            value={destination}
            onChange={(e) => setDestination(e.target.value)}
            required
          />
        </div>
        <div>
          <label className="block text-xs text-gray-500">위도</label>
          <input
            type="number"
            step="0.0001"
            className="w-28 rounded border px-2 py-1"
            value={latitude}
            onChange={(e) => setLatitude(e.target.value)}
            required
          />
        </div>
        <div>
          <label className="block text-xs text-gray-500">경도</label>
          <input
            type="number"
            step="0.0001"
            className="w-28 rounded border px-2 py-1"
            value={longitude}
            onChange={(e) => setLongitude(e.target.value)}
            required
          />
        </div>
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
