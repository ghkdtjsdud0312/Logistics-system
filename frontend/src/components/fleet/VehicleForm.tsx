import { FormEvent, useState } from 'react';
import { VehicleCreateRequest } from '@/types/vehicle';

interface Props {
  onSubmit: (request: VehicleCreateRequest) => Promise<boolean>;
}

const EMPTY: VehicleCreateRequest = {
  vehicleNumber: '',
  vehicleType: '',
  maxWeightKg: 0,
  maxVolumeM3: 0,
  hubDistanceKm: 0,
};

/** 차량 등록 폼 */
function VehicleForm({ onSubmit }: Props) {
  const [form, setForm] = useState<VehicleCreateRequest>(EMPTY);

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    if (await onSubmit(form)) setForm(EMPTY);
  };

  return (
    <form onSubmit={handleSubmit} className="flex flex-wrap items-end gap-2 rounded border p-3 text-sm">
      <div>
        <label className="block text-xs text-gray-500">차량번호</label>
        <input
          className="w-28 rounded border px-2 py-1"
          value={form.vehicleNumber}
          onChange={(e) => setForm({ ...form, vehicleNumber: e.target.value })}
          required
        />
      </div>
      <div>
        <label className="block text-xs text-gray-500">차량 유형</label>
        <input
          className="w-24 rounded border px-2 py-1"
          value={form.vehicleType}
          onChange={(e) => setForm({ ...form, vehicleType: e.target.value })}
          required
        />
      </div>
      <div>
        <label className="block text-xs text-gray-500">최대 중량(kg)</label>
        <input
          type="number"
          min={0}
          className="w-24 rounded border px-2 py-1"
          value={form.maxWeightKg || ''}
          onChange={(e) => setForm({ ...form, maxWeightKg: Number(e.target.value) || 0 })}
        />
      </div>
      <div>
        <label className="block text-xs text-gray-500">최대 부피(㎥)</label>
        <input
          type="number"
          min={0}
          step="0.1"
          className="w-24 rounded border px-2 py-1"
          value={form.maxVolumeM3 || ''}
          onChange={(e) => setForm({ ...form, maxVolumeM3: Number(e.target.value) || 0 })}
        />
      </div>
      <div>
        <label className="block text-xs text-gray-500">허브 거리(km)</label>
        <input
          type="number"
          min={0}
          className="w-24 rounded border px-2 py-1"
          value={form.hubDistanceKm || ''}
          onChange={(e) => setForm({ ...form, hubDistanceKm: Number(e.target.value) || 0 })}
        />
      </div>
      <button type="submit" className="rounded bg-gray-900 px-3 py-1.5 text-white">
        차량 등록
      </button>
    </form>
  );
}

export default VehicleForm;
