import { SCHEDULE_KIND_ICON, SCHEDULE_KIND_LABEL } from '@/constants/scheduleKind';
import { ScheduleKind } from '@/types/schedule';

interface KindFilterProps {
  selected: ScheduleKind[];
  onToggle: (kind: ScheduleKind) => void;
}

/** 일정 종류(배차/입고) 표시 여부 선택 */
function KindFilter({ selected, onToggle }: KindFilterProps) {
  return (
    <div className="flex gap-4 text-sm text-gray-600">
      {(Object.keys(SCHEDULE_KIND_LABEL) as ScheduleKind[]).map((kind) => (
        <label key={kind} className="flex cursor-pointer items-center gap-1.5">
          <input
            type="checkbox"
            checked={selected.includes(kind)}
            onChange={() => onToggle(kind)}
          />
          {SCHEDULE_KIND_ICON[kind]} {SCHEDULE_KIND_LABEL[kind]}
        </label>
      ))}
    </div>
  );
}

export default KindFilter;
