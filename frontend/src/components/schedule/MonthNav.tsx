import { BUTTON_SECONDARY } from '@/constants/styles';

interface MonthNavProps {
  label: string;
  onPrev: () => void;
  onNext: () => void;
  onToday: () => void;
}

/** 월 이동: 이전 / 오늘 / 다음 */
function MonthNav({ label, onPrev, onNext, onToday }: MonthNavProps) {
  return (
    <div className="flex items-center gap-2">
      <button className={BUTTON_SECONDARY} onClick={onPrev} aria-label="이전 달">
        ◀
      </button>
      <span className="min-w-28 text-center text-lg font-semibold text-gray-900">{label}</span>
      <button className={BUTTON_SECONDARY} onClick={onNext} aria-label="다음 달">
        ▶
      </button>
      <button className={BUTTON_SECONDARY} onClick={onToday}>
        오늘
      </button>
    </div>
  );
}

export default MonthNav;
