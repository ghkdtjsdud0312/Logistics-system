import { Driver } from '@/types/driver';

interface Props {
  drivers: Driver[];
  selectedDriverId: number | null;
  onChange: (driverId: number) => void;
  onConfirm: () => void;
  disabled: boolean;
}

/** 기사 배정 + 배차 확정 액션 */
function DriverSelect({ drivers, selectedDriverId, onChange, onConfirm, disabled }: Props) {
  return (
    <div className="flex items-center gap-2">
      <select
        className="rounded border px-2 py-1 text-sm"
        value={selectedDriverId ?? ''}
        onChange={(e) => onChange(Number(e.target.value))}
      >
        <option value="" disabled>
          기사를 선택하세요
        </option>
        {drivers.map((driver) => (
          <option key={driver.id} value={driver.id}>
            {driver.name}
          </option>
        ))}
      </select>
      <button
        type="button"
        disabled={disabled}
        onClick={onConfirm}
        className="rounded bg-gray-900 px-3 py-1.5 text-sm text-white disabled:opacity-40"
      >
        배차 확정
      </button>
    </div>
  );
}

export default DriverSelect;
